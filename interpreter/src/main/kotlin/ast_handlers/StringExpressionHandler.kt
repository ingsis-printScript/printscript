package org.example.interpreter.handlers

import org.example.ast.expressions.StringExpression
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

class StringExpressionHandler : ASTNodeHandler<StringExpression> {

    override fun handleExecution(node: StringExpression, executor: Executor) {
        executor.pushLiteral(node.value)
    }

    override fun handleValidation(node: StringExpression, validator: Validator) {
        validator.pushLiteral(Type.STRING)
    }
}
