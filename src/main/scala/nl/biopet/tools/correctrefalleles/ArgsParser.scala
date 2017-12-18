package nl.biopet.tools.correctrefalleles

import java.io.File

import nl.biopet.utils.tool.{AbstractOptParser, ToolCommand}

class ArgsParser(toolCommand: ToolCommand[Args])
    extends AbstractOptParser[Args](toolCommand) {
  opt[File]('I', "input") required () unbounded () valueName "<vcf file>" action {
    (x, c) =>
      c.copy(inputFile = x)
  } text "input vcf file"
  opt[File]('o', "output") required () unbounded () valueName "<vcf file>" action {
    (x, c) =>
      c.copy(outputFile = x)
  } text "output vcf file"
  opt[File]('R', "referenceFasta") required () unbounded () valueName "<fasta file>" action {
    (x, c) =>
      c.copy(referenceFasta = x)
  } text "Reference fasta file"
}
