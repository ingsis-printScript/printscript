package org.example.interpreter.handlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.functions.PrintFunction
import org.example.common.results.Success
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class PrintFunctionHandler : ASTNodeHandler<PrintFunction> {

    override fun handleExecution(node: PrintFunction, executor: Executor) {
        val value = when (val opt = node.value) {
            is OptionalExpression.NoExpression -> null
            is OptionalExpression.HasExpression -> executor.evaluate(opt.expression)
        }
        executor.printValue(value)

        executor.returnResult(Success(value))
    }

    override fun handleValidation(node: PrintFunction, validator: Validator) {
        val valueType = when (val opt = node.value) {
            is OptionalExpression.NoExpression -> null
            is OptionalExpression.HasExpression -> validator.evaluate(opt.expression)
        }
        validator.returnResult(Success(valueType))
    }
}
