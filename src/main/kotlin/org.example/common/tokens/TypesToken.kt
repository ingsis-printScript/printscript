package org.example.common.tokens

import org.example.common.Range
import org.example.common.tokens.enums.Types

data class TypesToken (
    var type: Types,
    val range: Range
): Token
