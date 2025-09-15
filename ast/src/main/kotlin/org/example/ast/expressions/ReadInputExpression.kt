package org.example.ast.expressions

import org.example.common.Range

class ReadInputExpression(
    val value: OptionalExpression,
    val range: Range
) : Expression
