package org.example.common.ast.statements

import org.example.common.Range
import org.example.common.ast.expressions.Expression
import org.example.common.ast.expressions.IdentifierExpression

data class VariableAssigner(
    val name: IdentifierExpression,
    val value: Expression,
    override val range: Range
    ): Statement {
}