package org.example.common.ast.expressions

import org.example.common.Range

// me suena medio raro que Identifier sea una Expression
class IdentifierExpression(
    val identifier: String,
    override val range: Range
): Expression{
}