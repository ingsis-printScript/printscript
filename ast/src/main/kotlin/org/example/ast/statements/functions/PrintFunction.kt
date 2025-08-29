package org.example.ast.statements.functions

import org.example.common.Range
import org.example.ast.expressions.Expression
import org.example.ast.expressions.IdentifierExpression

data class PrintFunction(
    val identifier: IdentifierExpression,
    val value: Expression,
    override val range: Range
): FunctionCall
