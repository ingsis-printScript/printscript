package org.example.cli.operations

import org.example.cli.util.LineIterator
import org.example.cli.Request
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Optional

class OperationDispatch {
    companion object {
        fun getOperation(args: Request): Optional<Operation> {
            return when (args.operation) {
                "validation" -> validation(args.version, args.inputSource)
                "execution" -> execution(args.version, args.inputSource)
                "formatting" -> Optional.of(FormattingOperation())
                "analyzing" -> analyzing(args.version, args.inputSource, args.configSource)
                else -> Optional.empty()
            }
        }

        private fun validation(version: String, source: String): Optional<Operation> {
            val reader = createReader(source)
            if (reader.isEmpty) return Optional.empty()
            return Optional.of(ValidationOperation(version, reader.get()))
        }

        private fun execution(version: String, source: String) : Optional<Operation> {
            val reader = createReader(source)
            if (reader.isEmpty) return Optional.empty()
            return Optional.of(ExecutionOperation(version, reader.get()))
        }

        private fun analyzing(version: String, source: String, config: String?) : Optional<Operation> {
            val reader = createReader(source)
            if (reader.isEmpty) return Optional.empty()
            val configStream = Files.newInputStream(Paths.get(config)) // check
            return Optional.of(AnalyzingOperation(version, reader.get(), configStream))
        }

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
