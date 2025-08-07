package org.example.common.tokens

data class IdentifierToken(val type: String,
                           val name: String,
                           val range: Range): Token {
}