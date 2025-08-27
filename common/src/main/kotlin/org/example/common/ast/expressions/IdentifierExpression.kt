package org.example.common.ast.expressions

import org.example.common.Position

data class IdentifierExpression(
    val name: String,
    val position: Position
) : Expression
