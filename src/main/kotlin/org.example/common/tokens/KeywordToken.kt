package org.example.common.tokens

import org.example.common.Range

data class KeywordToken(
    val kind: Keywords,
    val type: String,
    val range: Range
) : Token
