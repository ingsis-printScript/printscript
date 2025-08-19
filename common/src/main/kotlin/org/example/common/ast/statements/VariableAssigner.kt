package org.example.common.ast.statements

import org.example.common.Position
import org.example.common.ast.expressions.Expression
import org.example.common.ast.expressions.IdentifierExpression

class VariableAssigner(
    val name: IdentifierExpression,
    val value: Expression,
    override val position: Position
    ): Statement {
}