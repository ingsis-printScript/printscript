package org.example.ast.statements

import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.common.Range

data class VariableAssigner(
    val symbol: SymbolExpression,
    val value: OptionalExpression,
    override val range: Range
) : Statement {
}
