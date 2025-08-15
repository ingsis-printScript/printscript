package org.example.common.ast.expressions

import org.example.common.Range
import org.example.parser.enums.Type

class IdentifierExpression(
    val name: String,
    override val range: Range
): Expression{
}