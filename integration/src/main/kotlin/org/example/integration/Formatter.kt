package org.example.integration

import org.example.formatter.providers.FormatterVersionProvider
import org.example.lexer.provider.LexerVersionProvider
import java.io.InputStream
import java.io.Writer

class Formatter {
    fun format(
        src: Iterator<String>,
        version: String,
        config: InputStream,
        writer: Writer
    ) {
        val lexer = LexerVersionProvider().with(version).provide(src)
        val formatter = FormatterVersionProvider()
            .with(version).provide(
                lexer,
                writer,
                config
            )
        while (formatter.hasNext()) {
            formatter.getNext()
        }
    }
}
