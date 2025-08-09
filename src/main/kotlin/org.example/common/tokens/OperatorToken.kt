package org.example.common.tokens

import org.example.common.Range
import org.example.common.tokens.enums.Operator

data class OperatorToken(
    var type: Operator,
    val range: Range
) : Token