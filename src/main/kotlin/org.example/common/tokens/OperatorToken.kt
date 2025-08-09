package org.example.common.tokens

import org.example.common.Range

data class OperatorToken(
    var type: Operator,
    val range: Range
) : Token