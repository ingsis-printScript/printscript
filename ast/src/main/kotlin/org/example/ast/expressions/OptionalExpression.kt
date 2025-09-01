package org.example.ast.expressions

sealed class OptionalExpression {
    object NoExpression : OptionalExpression()
    data class HasExpression(val expression: Expression) : OptionalExpression()
}
