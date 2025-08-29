package org.example.ast.statements

import org.example.common.Range
import org.example.ast.expressions.Expression
import org.example.ast.expressions.IdentifierExpression

data class VariableAssigner(
    val name: IdentifierExpression,
    val value: Expression,
    override val range: Range
) : Statement
