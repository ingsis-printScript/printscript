package org.example.common.tokens

import org.example.common.Range

data class IdentifierToken(val type: String,
                           val name: String,
                           val range: Range
): Token