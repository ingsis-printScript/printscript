package org.example.cli

import org.example.cli.factory.PrintScript10Setup
import org.example.cli.factory.PrintScript11Setup
import org.example.cli.factory.SetupData
import org.example.cli.factory.ToolFactory
import org.example.cli.operations.AnalyzingOperation
import org.example.cli.operations.ExecutionOperation
import org.example.cli.operations.FormattingOperation
import org.example.cli.operations.Operation
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Optional

class OperationDispatch {
    companion object {
        fun getOperation(args: Request): Optional<Operation> {
            val from = ToolFactory(getSetup(args.version))

            return when (args.operation) {
                "validation" -> validation(from, args.inputSource)
                "execution" -> Optional.of(ExecutionOperation())
                "formatting" -> Optional.of(FormattingOperation())
                "analyzing" -> Optional.of(AnalyzingOperation())
                else -> Optional.empty()
            }
        }

        private fun getSetup(version: String): SetupData {
            when (version) {
                "1.0" -> return PrintScript10Setup()
                "1.1" -> return PrintScript11Setup()
            }
            return PrintScript10Setup() // default version
        }

        private fun validation(from: ToolFactory, source: String): Optional<Operation> {
            val reader = createReader(source)
            if (reader.isEmpty) return Optional.empty()
            // return Optional.of(ValidationOperation(from.lexer(reader.get()), from.parser()))
            return Optional.empty() // TODO
        }

        // I hate the "if" condition -> sever method from File... eventually
        private fun createReader(pathStr: String): Optional<Iterator<String>> {
            val path = Paths.get(pathStr)
            return if (Files.exists(path)) {
                Optional.of(LineIterator(path))
            } else {
                Optional.empty()
            }
        }
    }
}
