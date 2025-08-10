package org.example.common.ast.expressions

import org.example.common.Range

class IdentifierExpression(
    val identifier: String,
    override val range: Range
): Expression{
}