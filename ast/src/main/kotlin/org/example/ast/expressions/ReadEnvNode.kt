package org.example.ast.expressions

import org.example.common.enums.Type

class ReadEnvNode(
    val varName: String,
    val expectedType: Type
) : Expression
