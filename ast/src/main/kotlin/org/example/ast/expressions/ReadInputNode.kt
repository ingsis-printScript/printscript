package org.example.ast.expressions

import org.example.common.Range
import org.example.common.enums.Type

class ReadInputNode(
    val prompt: OptionalExpression,
    val expectedType: Type,
    val range: Range
) : Expression
