package org.example.common.ast.statements

import org.example.common.Range
import org.example.common.ast.expressions.Expression
import org.example.common.ast.expressions.IdentifierExpression

class FunctionCall(
    val identifier: IdentifierExpression,
    val value: Expression,
    override val range: Range
): Statement {
}