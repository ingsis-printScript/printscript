package org.example.integration

import org.example.common.ErrorHandler
import org.example.common.results.Error
import org.example.lexer.provider.LexerVersionProvider
import org.example.parser.TokenBuffer
import org.example.parser.provider.ParserVersionProvider

class Parser {
    fun validate(
        src: Iterator<String>,
        version: String,
        errorHandler: ErrorHandler,
    ) {
        val lexer = LexerVersionProvider().with(version).provide(src)
        val parser = ParserVersionProvider().with(version).provide(TokenBuffer(lexer))

        while (parser.hasNext()) {
            val result = parser.getNext()
            if (result is Error) {
                errorHandler.handleError(result.message)
            }
        }
    }
}