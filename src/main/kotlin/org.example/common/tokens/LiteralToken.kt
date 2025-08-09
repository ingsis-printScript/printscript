package org.example.common.tokens

import org.example.common.Range
import org.example.common.tokens.enums.LiteralType

data class LiteralToken<T>(
    val type: LiteralType,
    val raw: String,
    val value: T,
    val range: Range
): Token
