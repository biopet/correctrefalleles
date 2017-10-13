package nl.biopet.tools.correctrefalleles

import htsjdk.samtools.reference.IndexedFastaSequenceFile
import htsjdk.variant.variantcontext._
import htsjdk.variant.variantcontext.writer.{AsyncVariantContextWriter, VariantContextWriterBuilder}
import htsjdk.variant.vcf.VCFFileReader

import nl.biopet.utils.tool.ToolCommand

import scala.collection.JavaConversions._

object CorrectRefAlleles extends ToolCommand {
  def main(args: Array[String]): Unit = {
    val parser = new ArgsParser(toolName)
    val cmdArgs =
      parser.parse(args, Args()).getOrElse(throw new IllegalArgumentException)

    logger.info("Start")

    correctRefAlleles(cmdArgs)

    logger.info("Done")
  }

  def correctRefAlleles(cmdArgs: Args): Unit = {
    logger.warn("This tool will only look ad the GT field and ignores the rest")
    val referenceFile = new IndexedFastaSequenceFile(cmdArgs.referenceFasta)

    val reader = new VCFFileReader(cmdArgs.inputFile, false)
    val header = reader.getFileHeader
    val writer = new AsyncVariantContextWriter(
      new VariantContextWriterBuilder()
        .setOutputFile(cmdArgs.outputFile)
        .setReferenceDictionary(header.getSequenceDictionary)
        .build)
    writer.writeHeader(header)

    for (record <- reader) {
      val ref = referenceFile
        .getSubsequenceAt(record.getContig, record.getStart, record.getEnd)
        .getBaseString
      val correct = record.getAlleles.forall { allele =>
        if (allele.isReference) allele.getBaseString == ref
        else allele.getBaseString != ref
      }
      if (correct) writer.add(record)
      else {
        val alleles = record.getAlleles.map { a =>
          val bases = a.getBaseString
          Allele.create(bases, bases == ref)
        }
        val genotypes = record.getGenotypes.map { g =>
          new GenotypeBuilder(g.getSampleName, g.getAlleles.map { a =>
            if (a.isCalled) alleles.find(_.getBaseString == a.getBaseString).get
            else Allele.NO_CALL
          }).make()
        }
        val newRecord = new VariantContextBuilder(record)
          .alleles(alleles)
          .genotypes(genotypes)
          .make()
        writer.add(newRecord)
      }
    }

    referenceFile.close()
    writer.close()
    reader.close()
  }
}
