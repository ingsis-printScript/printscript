package org.example.common.tokens

import org.example.common.Range
import org.example.common.tokens.enums.Types
// mismo tema que Identifier, Operator y alguno más (KIND vs TYPE)
data class TypeToken (
    var kind: Types,
    val range: Range
): Token
