package org.example.ast.expressions

import org.example.common.Position

data class SymbolExpression(
    val name: String,
    val position: Position
) : Expression
