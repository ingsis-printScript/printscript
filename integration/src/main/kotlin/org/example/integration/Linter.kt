package org.example.integration

import org.example.interpreter.output.ErrorHandler
import org.example.lexer.provider.LexerVersionProvider
import org.example.linter.provider.LinterVersionProvider
import org.example.parser.TokenBuffer
import org.example.parser.provider.ParserVersionProvider

class Linter {
    fun lint(
        src: Iterator<String>,
        version: String,
        config: Iterator<String>,
        handler: ErrorHandler,
    ) {
        val lexer = LexerVersionProvider().with(version).provide(src)
        val parser = ParserVersionProvider().with(version).provide(TokenBuffer(lexer))
        val linter = LinterVersionProvider()
            .with(version).provide(
                parser,
                config,
                handler
            )
        while (linter.analyze()) {
            // TODO
        }
    }
}