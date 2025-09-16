package org.example.cli.operations

import org.example.cli.Request
import org.example.cli.util.CliProgressReporter
import org.example.cli.util.LineIterator
import org.example.cli.util.ProgressNotifyingIterator
import java.io.InputStream
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Optional

class OperationDispatch {
    companion object {
        fun getOperation(args: Request): Optional<Operation> {
            return when (args.operation) {
                "validation" -> validation(args.version, args.inputSource)
                "execution" -> execution(args.version, args.inputSource)
                "formatting" -> formatting(args.version, args.inputSource, args.configSource)
                "analyzing" -> analyzing(args.version, args.inputSource, args.configSource)
                else -> Optional.empty()
            }
        }

        private fun validation(version: String, source: String): Optional<Operation> {
            val reader = createSourceReader(source)
            if (reader.isEmpty) return Optional.empty()
            return Optional.of(ValidationOperation(version, reader.get()))
        }

        private fun execution(version: String, source: String): Optional<Operation> {
            val reader = createSourceReader(source)
            if (reader.isEmpty) return Optional.empty()
            return Optional.of(ExecutionOperation(version, reader.get()))
        }

        private fun analyzing(version: String, source: String, config: String?): Optional<Operation> {
            val reader = createSourceReader(source)
            if (reader.isEmpty) return Optional.empty()
            val configStream = Files.newInputStream(Paths.get(config)) // check
            return Optional.of(AnalyzingOperation(version, reader.get(), configStream))
        }

        private fun formatting(version: String, source: String, config: String?): Optional<Operation> {
            val reader = createSourceReader(source)
            if (reader.isEmpty) return Optional.empty()

            val configStream: InputStream =
                if (config != null) {
                    Files.newInputStream(Paths.get(config))
                } else {
                    java.io.ByteArrayInputStream(ByteArray(0))
                }

            val srcPath = Paths.get(source)
            val outPath = buildFormattedOutputPath(srcPath)

            System.err.println("Writing formatted output in: ${outPath.toAbsolutePath()}")

            val writer: Writer = Files.newBufferedWriter(
                outPath,
                java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING,
                java.nio.file.StandardOpenOption.WRITE
            )

            return Optional.of(FormattingOperation(version, reader.get(), writer, configStream))
        }

        private fun createSourceReader(pathStr: String): Optional<Iterator<String>> {
            val path = Paths.get(pathStr)
            return if (Files.exists(path)) {
                val base = LineIterator(path)
                val reporter = CliProgressReporter.fromPath(path)
                Optional.of(ProgressNotifyingIterator(base, reporter))
            } else {
                Optional.empty()
            }
        }

        private fun buildFormattedOutputPath(src: java.nio.file.Path): java.nio.file.Path {
            val name = src.fileName.toString()
            val dot = name.lastIndexOf('.')
            val (base, ext) = if (dot > 0) name.substring(0, dot) to name.substring(dot) else name to ""
            val outName = "formatted-$base$ext"
            return src.resolveSibling(outName)
        }
    }
}
