package org.example.ast.statements.functions

import org.example.common.Range
import org.example.ast.expressions.Expression
import org.example.ast.expressions.SymbolExpression

data class PrintFunction(
    val symbol: SymbolExpression,
    val value: Expression,
    override val range: Range
): FunctionCall
