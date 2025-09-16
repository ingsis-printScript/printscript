package org.example.interpreter.providers

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.PrintScriptIterator
import org.example.common.results.Result
import org.example.interpreter.Executor
import org.example.interpreter.Interpreter
import org.example.interpreter.Validator
import org.example.interpreter.input.InputProvider
import org.example.common.ErrorHandler
import org.example.interpreter.output.OutputPrinter

class InterpreterProvider10 : InterpreterProvider {
    override fun provide(
        iterator: PrintScriptIterator<Result>,
        inputProvider: InputProvider,
        outputPrinter: OutputPrinter,
        errorHandler: ErrorHandler
    ): Interpreter {
        val supportedNodes = supportedNodes()

        val validator = Validator(errorHandler)
        val executor = Executor(inputProvider, outputPrinter, errorHandler)

        return Interpreter(iterator, validator, executor, supportedNodes)
    }

    private fun supportedNodes(): Set<Class<out ASTNode>> {
        return setOf(
            BinaryExpression::class.java,
            NumberExpression::class.java,
            StringExpression::class.java,
            PrintFunction::class.java,
            SymbolExpression::class.java,
            VariableDeclarator::class.java,
            VariableAssigner::class.java
        )
    }
}
