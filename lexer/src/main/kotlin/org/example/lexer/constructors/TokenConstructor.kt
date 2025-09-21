package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.Token
import java.util.*

interface TokenConstructor {
    fun constructToken(input: String, position: Position): Optional<Token>
}
