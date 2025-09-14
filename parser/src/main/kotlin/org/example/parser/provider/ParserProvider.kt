package org.example.parser.provider

import org.example.parser.Parser
import org.example.parser.TokenBuffer

interface ParserProvider {
    fun provide(tokenBuffer: TokenBuffer): Parser
}
