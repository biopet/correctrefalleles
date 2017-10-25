# Manual

## Introduction
This tool corrects the reference alleles in a VCF file, if the mentioned allele does not match up with the provided
reference.

## Examples
To run the tool:
```bash
java -jar CorrectRefAlleles-version -I input.vcf -o output.vcf -R reference.fa
```

To open the help:
```bash
java -jar CorrectRefAlleles-version --help
General Biopet options


Options for CorrectRefAlleles

Usage: CorrectRefAlleles [options]

  -l, --log_level <value>  Level of log information printed. Possible levels: 'debug', 'info', 'warn', 'error'
  -h, --help               Print usage
  -v, --version            Print version
  -I, --input <vcf file>   input vcf file
  -o, --output <vcf file>  output vcf file
  -R, --referenceFasta <fasta file>
                           Reference fasta file

```

## Output
A VCf file with corrected reference alleles