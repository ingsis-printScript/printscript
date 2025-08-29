package org.example.common.ast.statements.functions

import org.example.common.Range
import org.example.common.ast.expressions.Expression
import org.example.common.ast.expressions.IdentifierExpression

data class PrintFunction(
    val identifier: IdentifierExpression,
    val value: Expression,
    override val range: Range
): FunctionCall
