package org.example.common.tokens

import org.example.common.Range
import org.example.common.tokens.enums.Keywords

data class KeywordToken(
    val kind: Keywords,
    val type: String,
    val range: Range
) : Token
