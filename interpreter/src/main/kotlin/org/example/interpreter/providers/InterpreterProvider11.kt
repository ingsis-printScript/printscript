package org.example.interpreter.providers

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.ReadEnvNode
import org.example.ast.expressions.ReadInputNode
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.PrintScriptIterator
import org.example.interpreter.Executor
import org.example.interpreter.Interpreter
import org.example.interpreter.Validator
import org.example.interpreter.asthandlers.BooleanExpressionHandler
import org.example.interpreter.asthandlers.NumberExpressionHandler
import org.example.interpreter.asthandlers.PrintFunctionHandler
import org.example.interpreter.asthandlers.ReadEnvNodeHandler
import org.example.interpreter.asthandlers.ReadInputNodeHandler
import org.example.interpreter.asthandlers.StringExpressionHandler
import org.example.interpreter.asthandlers.SymbolExpressionHandler
import org.example.interpreter.asthandlers.VariableAssignerHandler
import org.example.interpreter.asthandlers.VariableDeclaratorHandler
import org.example.interpreter.asthandlers.VariableImmutableDeclaratorHandler
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.handlers.BinaryExpressionHandler
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.ErrorHandler
import org.example.interpreter.output.OutputPrinter

class InterpreterProvider11 : InterpreterProvider {
    override fun provide(iterator: PrintScriptIterator<ASTNode>): Interpreter {

        val handlers = createHandlers()

        val errorHandler = object : ErrorHandler {
            val errors = mutableListOf<String>()
            override fun handleError(message: String) {
                errors.add(message)
            }
        }

        val fakePrinter = object : OutputPrinter {
            val printed = mutableListOf<String>()
            override fun print(value: String) {
                printed.add(value)
            }
        }

        val fakeInputProvider = object : InputProvider {
            override fun readInput(prompt: String) = "123"
        }

        val validator = Validator(handlers, errorHandler) //queremos quese errorHandler?
        val executor = Executor(handlers, fakeInputProvider, fakePrinter, errorHandler)


        val interpreter = Interpreter(iterator, validator, executor)
        return interpreter
    }

    private fun createHandlers(): Map<Class<out ASTNode>, ASTNodeHandler<*>> {
        val handlers = mapOf(
            BooleanExpression::class.java to BooleanExpressionHandler(),
            ReadEnvNode::class.java to ReadEnvNodeHandler(),
            ReadInputNode::class.java to ReadInputNodeHandler(),
            VariableImmutableDeclarator::class.java to VariableImmutableDeclaratorHandler(),
            BinaryExpression::class.java to BinaryExpressionHandler(),
            NumberExpression::class.java to NumberExpressionHandler(),
            PrintFunction::class.java to PrintFunctionHandler(),
            StringExpression::class.java to StringExpressionHandler(),
            SymbolExpression::class.java to SymbolExpressionHandler(),
            VariableDeclarator::class.java to VariableDeclaratorHandler(),
            VariableAssigner::class.java to VariableAssignerHandler())
        return handlers as Map<Class<out ASTNode>, ASTNodeHandler<*>>
    }
}
