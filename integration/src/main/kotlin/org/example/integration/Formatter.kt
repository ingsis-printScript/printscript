package org.example.integration

import org.example.formatter.providers.FormatterVersionProvider
import org.example.interpreter.input.InputProvider
import org.example.lexer.provider.LexerVersionProvider
import org.example.parser.TokenBuffer
import org.example.parser.provider.ParserVersionProvider

class Formatter {
    fun execute(
        src: Iterator<String>,
        version: String,
        config: Iterator<String>,
        provider: InputProvider
    ) {
        val lexer = LexerVersionProvider().with(version).provide(src)
        val parser = ParserVersionProvider().with(version).provide(TokenBuffer(lexer))
        val formatter = FormatterVersionProvider()
            .with(version).provide(
                parser
            )
        while (formatter.hasNext()) {
            formatter.getNext() // todo: duda con que sea un Iterator<Result> teniendo emitter y handler
        }
    }
}
