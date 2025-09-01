package org.example.ast.statements.functions

import org.example.common.Range
import org.example.ast.expressions.OptionalExpression

data class PrintFunction(
    val value: OptionalExpression,
    override val range: Range
): FunctionCall
