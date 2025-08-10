package org.example.common.tokens

import org.example.common.Range
import org.example.common.tokens.enums.Operator
// TODO(inconsistencia entre uso de KIND y TYPE en los tokens)
// KIND es "tipo de LiteralToken" -> sale del ENUM, por ej.
// TYPE es "tipo de token" -> String, que coincide con el tipo de token (la clase)
// renombré type a kind por un uso que le doy
data class OperatorToken(
    var kind: Operator,
    val range: Range
) : Token