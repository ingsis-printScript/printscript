package org.example.integration

import org.example.formatter.providers.FormatterVersionProvider
import org.example.lexer.provider.LexerVersionProvider
import org.example.parser.TokenBuffer
import org.example.parser.provider.ParserVersionProvider
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
        val parser = ParserVersionProvider().with(version).provide(TokenBuffer(lexer))
        val formatter = FormatterVersionProvider()
            .with(version).provide(
                parser,
                writer,
                config
            )
        while (formatter.hasNext()) {
            formatter.getNext()
        }
    }
}
