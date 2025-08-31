package org.example.parser

import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.TokenType
import org.example.token.Token


class TokenFactory {

    val basicPosition = Position(1, 1)

    fun createKeyword(value: String): Token = Token(TokenType.KEYWORD, value, basicPosition)

    fun createOperator(value: String): Token = Token(TokenType.OPERATOR, value, basicPosition)

    fun createPunctuation(value: String): Token = Token(TokenType.PUNCTUATION, value, basicPosition)

    fun createNumber(value: String): Token = Token(TokenType.NUMBER, value, basicPosition)

    fun createString(value: String): Token = Token(TokenType.STRING, value, basicPosition)

    fun createSymbol(value: String): Token = Token(TokenType.SYMBOL, value, basicPosition)

    fun createEquals(): Token = Token(TokenType.PUNCTUATION, "=", basicPosition)

    fun createSemicolon(): Token = Token(TokenType.PUNCTUATION, ";", basicPosition)

}
