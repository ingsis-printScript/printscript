package org.example.interpreter.ast_handlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.functions.PrintFunction
import org.example.interpreter.Executor
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.Validator

class PrintFunctionHandler : ASTNodeHandler<PrintFunction> {

    override fun handleExecution(node: PrintFunction, executor: Executor) {
        val value = when (val opt = node.value) {
            is OptionalExpression.NoExpression -> null
            is OptionalExpression.HasExpression -> executor.evaluate(opt.expression)
        }

        if (value != null) {
            executor.printValue(value)
        }
    }

    override fun handleValidation(node: PrintFunction, validator: Validator) {
        when (val opt = node.value) {
            is OptionalExpression.NoExpression -> validator.pushLiteral(null)
            is OptionalExpression.HasExpression -> {
                val valueType = validator.evaluate(opt.expression)
                validator.pushLiteral(valueType)
            }
        }
    }
}
