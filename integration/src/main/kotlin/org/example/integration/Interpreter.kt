package org.example.integration

import org.example.ast.statements.Condition
import org.example.common.ErrorHandler
import org.example.common.results.Error
import org.example.common.results.Success
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
        handler: ErrorHandler, // keep throws??
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
            val node = interpreter.getNext()
            val r = if (node is Error) node.message else (node as Success<*>).value.toString()
            System.err.println("[DEBUG] Executing node: ${node.javaClass.simpleName} -> $r")
            if (node is Success<*> && node.value is Condition) {
                val n = node.value as Condition
                System.err.println("[DEBUG] - Condition with ${n.ifBlock.size} if statements and ${n.elseBlock?.size ?: 0} else statements")
            }
        }
    }
}
