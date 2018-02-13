/*
 * Copyright (c) 2017 Biopet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nl.biopet.tools.correctrefalleles

import htsjdk.samtools.reference.IndexedFastaSequenceFile
import htsjdk.variant.variantcontext._
import htsjdk.variant.variantcontext.writer.{
  AsyncVariantContextWriter,
  VariantContextWriterBuilder
}
import htsjdk.variant.vcf.VCFFileReader

import nl.biopet.utils.tool.ToolCommand

import scala.collection.JavaConversions._

object CorrectRefAlleles extends ToolCommand[Args] {
  def emptyArgs: Args = Args()
  def argsParser = new ArgsParser(this)
  def main(args: Array[String]): Unit = {
    val cmdArgs = cmdArrayToArgs(args)

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
            if (a.isCalled)
              alleles.find(_.getBaseString == a.getBaseString).get
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

  def descriptionText: String =
    """
      |This tool corrects the reference alleles in a VCF file.
      |Some tools switch the `REF` and `ALT` alleles when creating a vcf.
      |This tool checks what the reference allele is at the given position.
      |It then checks whether this matches up with the `REF` and `ALT` column
      |and switches them if necessary.
      |
    """.stripMargin

  def manualText: String =
    """
      |This tool needs a reference genome to check if the stated `REF` allele
      |at a given position is correct. The contig names of the reference and the
      |input VCF should match.
      |
    """.stripMargin

  def exampleText: String =
    s"""
       |To check if `input.vcf` `REF` and `POS` columns matches with `reference.fa`
       | and give the corrected vcf as `output.vcf`:
       |${example("-I", "input.vcf", "-o", "output.vcf", "-R", "reference.fa")}
     """.stripMargin
}
