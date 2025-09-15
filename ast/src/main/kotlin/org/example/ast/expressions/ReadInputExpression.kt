package org.example.ast.expressions

import org.example.common.Range
import org.example.common.enums.Type

class ReadInputExpression(
    val value: OptionalExpression,
    val range: Range
) : Expression
