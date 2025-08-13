package org.example.common.ast.statements

import org.example.common.Range
import org.example.common.ast.expressions.Expression

class FunctionCall(
    val identifier: String,
    val arguments: List<Expression>,
    override val range: Range
): Statement {
}