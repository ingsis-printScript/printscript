package org.example.common.tokens

import org.example.common.Range

data class Token(
    val type: TokenType,
    val value: String,
    val range: Range,
)

//(punctuation)
// TODO(inconsistencia entre uso de KIND y TYPE en los tokens)
// KIND es "tipo de LiteralToken" -> sale del ENUM, por ej.
// TYPE es "tipo de token" -> String, que coincide con el tipo de token (la clase)
// si no queremos TYPE, que no se use nunca. Sino usémoslo siempre.


//(literal)
// TODO(inconsistencia entre uso de KIND y TYPE en los tokens)
// KIND es "tipo de LiteralToken" -> sale del ENUM, por ej.
// TYPE es "tipo de token" -> String, que coincide con el tipo de token (la clase)
