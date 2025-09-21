package org.example.integration

import org.example.common.ErrorHandler
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.OutputPrinter
import org.example.interpreter.providers.InterpreterVersionProvider
import org.example.lexer.provider.LexerVersionProvider
import org.example.parser.TokenBuffer
import org.example.parser.provider.ParserVersionProvider

class Interpreter {
    fun execute(
        src: Iterator<String>,
        version: String,
        emitter: OutputPrinter,
        handler: ErrorHandler,
        provider: InputProvider
    ) {
        val lexer = LexerVersionProvider().with(version).provide(src)
        val parser = ParserVersionProvider().with(version).provide(TokenBuffer(lexer))
        val interpreter = InterpreterVersionProvider()
            .with(version).provide(
                parser,
                provider,
                emitter,
                handler
            )
        while (interpreter.hasNext()) {
            interpreter.getNext()
        }
    }
}
