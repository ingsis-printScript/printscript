package org.example.ast.expressions

import org.example.common.enums.Type

class ReadInputNode(
    val prompt: String,
    val expectedType: Type
) : Expression {
}
