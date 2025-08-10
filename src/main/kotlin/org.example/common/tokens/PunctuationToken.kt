package org.example.common.tokens

import org.example.common.Range
import org.example.common.tokens.enums.Punctuation

// TODO(inconsistencia entre uso de KIND y TYPE en los tokens)
// KIND es "tipo de LiteralToken" -> sale del ENUM, por ej.
// TYPE es "tipo de token" -> String, que coincide con el tipo de token (la clase)
// si no queremos TYPE, que no se use nunca. Sino usémoslo siempre.
data class PunctuationToken(
    val kind: Punctuation,
    val range: Range
): Token