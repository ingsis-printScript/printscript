package org.example.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import org.example.cli.operations.Operation


fun main(args: Array<String>) = PrintScriptCli().main(args)

class PrintScriptCli : CliktCommand(name = "printscript") {

    private val operation by argument(
        name = "operation",
        help = "Operation to perform"
    ).choice("validation", "execution", "formatting", "analyzing")

    // don't love tying CLI to FILE, but it'll do for now (o sea... la consigna dice file, no input)
    private val sourceFile by argument(
        name = "file",
        help = "Source file to process"
    ).file(mustExist = true, canBeDir = false, mustBeReadable = true)

    // idem
    private val configFile by option(
        "--config", "-c",
        help = "Configuration file for appropriate operation"
    ).file(mustExist = true, canBeDir = false, mustBeReadable = true)

    private val version by option(
        "--version", "-v",
        help = "PrintScript version"
    ).default("1.0")

    override fun run() {
        echo("Operation: $operation")
        echo("Source: ${sourceFile.name}")
        echo("Version: $version")
        configFile?.let { echo("Config: ${it.name}") }

        val operator = Operation.get(operation)

        if (operator.isPresent) { // TODO: determinar que hago con config y Result
            val config = configFile?.name
            val request = Request(sourceFile.name, version, config)
            val result: Result<String> = operator.get().execute(request)
            // TODO: process result and echo output...

        } else echo("Invalid operation")
    }
}
