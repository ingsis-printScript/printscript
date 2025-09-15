package org.example.ast.expressions

import org.example.common.enums.Type

class ReadEnvExpression(
    val varName: String,
    val expectedType: Type
) : Expression
