package org.example.ast.statements

import org.example.common.Range
import org.example.ast.expressions.Expression
import org.example.ast.expressions.SymbolExpression
import org.example.common.enums.Type

data class VariableDeclarator(
    val symbol: SymbolExpression,
    val type: Type,
    override val range: Range,
    val value: Expression? = null
) : Statement
