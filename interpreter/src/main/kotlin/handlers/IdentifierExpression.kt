package org.example.interpreter.handlers

import org.example.common.ast.expressions.IdentifierExpression
import org.example.interpreter.Executor


class IdentifierExpression : ASTNodeHandler<IdentifierExpression> {
    override fun handleExecution(node: IdentifierExpression, executor: Executor) {
        val value = executor.lookupVariable(node.name)
        executor.pushLiteral(value)
    }
}