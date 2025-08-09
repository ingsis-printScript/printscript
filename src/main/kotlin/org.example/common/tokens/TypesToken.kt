package org.example.common.tokens

import org.example.common.Range

data class TypesToken (
    var type: Types,
    val range: Range
): Token
