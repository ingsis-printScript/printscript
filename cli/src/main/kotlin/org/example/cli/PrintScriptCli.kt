package org.example.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import org.example.cli.operations.OperationDispatch

fun main(args: Array<String>) {
    val testArgs = if (args.isEmpty()) {
        arrayOf(
            "execution",
            "test.ps",
            "--version", "1.1"
        )
    } else {
        args
    }

    PrintScriptCli().main(testArgs)
}

class PrintScriptCli : CliktCommand(name = "printscript") {

    private val operation by argument(
        name = "operation",
        help = "Operation to perform"
    ).choice("validation", "execution", "formatting", "analyzing")

    private val sourceFile by argument(
        name = "file",
        help = "Source file to process"
    ).file(mustExist = true, canBeDir = false, mustBeReadable = true)

    private val configFile by option(
        "--config",
        "-c",
        help = "Configuration file for appropriate operation"
    ).file(mustExist = true, canBeDir = false, mustBeReadable = true)

    private val version by option(
        "--version",
        "-v",
        help = "PrintScript version"
    ).default("1.0")

    override fun run() {
        echo("Operation: $operation")
        echo("Source: ${sourceFile.name}")
        echo("Version: $version")
        configFile?.let { echo("Config: ${it.name}") }

        val request = Request(operation, sourceFile.absolutePath, version, configFile?.absolutePath)

        val operator = OperationDispatch.getOperation(request)

        if (operator.isPresent) {
            operator.get().execute()
        } else {
            echo("Invalid operation")
        }
    }
}
