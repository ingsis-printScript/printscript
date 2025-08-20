package org.example.common.ast.expressions

import org.example.common.Position

class IdentifierExpression(
    val name: String,
    override val position: Position
): Expression{
}