package org.example.cli.operations

import org.example.cli.Request


import java.util.Optional


interface Operation {
    fun execute(args: Request) : Result<String>

    companion object {
        fun get(operation: String) : Optional<Operation> {
            when (operation) {
                "validation" -> return Optional.of(ValidationOperation())
                "execution" -> return Optional.of(ExecutionOperation())
                "formatting" -> return Optional.of(FormattingOperation())
                "analyzing" -> return Optional.of(AnalyzingOperation())
            }
            return Optional.empty()
        }
    }
}
