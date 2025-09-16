package org.example.cli.util

import org.example.interpreter.output.OutputPrinter

class CliPrinter : OutputPrinter {
    override fun print(output: String) {
        System.err.println(output)
        System.err.flush()
    }
}
