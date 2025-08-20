package org.example.common.ast.statements

import org.example.common.Position
import org.example.common.ast.expressions.Expression

class FunctionCall(
    val identifier: String,
    val arguments: List<Expression>,
    override val position: Position
): Statement {
}