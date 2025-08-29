package org.example.ast.statements

import org.example.common.Range
import org.example.ast.expressions.Expression
import org.example.ast.expressions.IdentifierExpression
import org.example.common.enums.Type

data class VariableDeclarator(
    val identifier: IdentifierExpression,
    val type: Type,
    override val range: Range,
    val value: Expression? = null
) : Statement
