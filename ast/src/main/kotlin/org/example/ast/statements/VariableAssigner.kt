package org.example.ast.statements

import org.example.common.Range
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression

data class VariableAssigner(
    val symbol: SymbolExpression,
    val value: OptionalExpression,
    override val range: Range
) : Statement
