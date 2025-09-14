package org.example.ast.statements.functions

import org.example.ast.expressions.OptionalExpression
import org.example.common.Range

data class PrintFunction(
    val value: OptionalExpression,
    override val range: Range
) : FunctionCall
