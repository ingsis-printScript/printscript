package org.example.ast.expressions

import org.example.common.Range

class ReadEnvExpression(
    val value: OptionalExpression,
    val range: Range
) : Expression
