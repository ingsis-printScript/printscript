package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.enums.Keywords
import org.example.common.tokens.Token
import java.util.*

class KeywordTokenDetector : TokenDetector {

    //objetivo es identificar a partir del formato del string q se le pasa que Token es.
    //como: se tiene fijar a partir del enum de keywords si coincide con el string que recibio
    override fun detect(string: String): Optional<Token> {

        val upperString = string.uppercase()

        // Iteramos sobre todos los valores del enum Keywords
        for (keyword in Keywords.values()) {
            // Si encontramos una coincidencia, retornamos el nombre del keyword
            if (keyword.name == upperString) {
                return Optional.of(KeywordToken(keyword, "", Range(0,0) ))
            }
        }
        return Optional.empty<Token>()

    }
}