package org.example.common.tokens

import org.example.common.Range
import org.example.common.tokens.enums.LiteralType

// TODO(inconsistencia entre uso de KIND y TYPE en los tokens)
// KIND es "tipo de LiteralToken" -> sale del ENUM, por ej.
// TYPE es "tipo de token" -> String, que coincide con el tipo de token (la clase)
data class LiteralToken<T>(
    val type: LiteralType,
    val raw: String,
    val value: T,
    val range: Range
): Token
