package nl.biopet.tools.correctrefalleles

import java.io.File

case class Args(inputFile: File = null,
                outputFile: File = null,
                referenceFasta: File = null)
