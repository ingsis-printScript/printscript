package org.example.ast.expressions

import org.example.common.Range
import org.example.common.enums.Type

class ReadEnvExpression(
    val varName: String,
    val range: Range
) : Expression
