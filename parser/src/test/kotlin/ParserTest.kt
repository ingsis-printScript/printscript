package org.example.parser

import org.example.common.Position
import org.example.common.tokens.Token
import org.example.common.enums.TokenType
import org.example.parser.exceptions.SyntaxException
import org.example.parser.parsers.functionparsers.PrintParser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ParserTest {
// crear tokenFactory
    // buscar manera de crear un astfactory (generalizar. mientras mas generico mejor.)
    // todo cambiar throws / analizar throws es lo mejor (medio raro)
    private lateinit var parser: Parser

    @BeforeEach
    fun setUp() {
        val statementParsers = listOf(
            VariableDeclarationParser(),
            VariableDeclarationAssignationParser(),
            VariableAssignationParser(),
            PrintParser()
        )

        parser = Parser(statementParsers)
    }

    private fun createToken(type: TokenType, value: String, line: Int = 1, column: Int = 1): Token {
        return Token(type, value, Position(line, column))
    }

    private fun createSemicolon(): Token = createToken(TokenType.PUNCTUATION, ";")

    @Test
    fun `parse empty token list returns empty program`() {
        val emptyTokens = emptyList<Token>()

        val result = parser.parse(emptyTokens)

        assertEquals(0, result.statements.size)
        assertTrue(result.statements.isEmpty())
    }

    @Test
    fun `parse variable declaration without assignment`() {
        val tokens = listOf(
            createToken(TokenType.KEYWORD, "let"),
            createToken(TokenType.SYMBOL, "x"),
            createToken(TokenType.PUNCTUATION, ":"),
            createToken(TokenType.SYMBOL, "number"),
            createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse variable declaration with assignment`() {
        val tokens = listOf(
            createToken(TokenType.KEYWORD, "let"),
            createToken(TokenType.SYMBOL, "x"),
            createToken(TokenType.PUNCTUATION, ":"),
            createToken(TokenType.SYMBOL, "number"),
            createToken(TokenType.PUNCTUATION, "="),
            createToken(TokenType.NUMBER, "5"),
            // meter expresion
            createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse variable declaration with string value`() {
        val tokens = listOf(
            createToken(TokenType.KEYWORD, "let"),
            createToken(TokenType.SYMBOL, "name"),
            createToken(TokenType.PUNCTUATION, ":"),
            createToken(TokenType.SYMBOL, "String"),
            createToken(TokenType.PUNCTUATION, "="),
            createToken(TokenType.STRING, "John"),
            createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse simple variable assignment`() {
        val tokens = listOf(
            createToken(TokenType.SYMBOL, "x"),
            createToken(TokenType.PUNCTUATION, "="),
            createToken(TokenType.NUMBER, "10"),
            createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse variable assignment with another variable`() {
        val tokens = listOf(
            createToken(TokenType.SYMBOL, "y"),
            createToken(TokenType.PUNCTUATION, "="),
            createToken(TokenType.SYMBOL, "x"),
            createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse function call without parameters`() {
        val tokens = listOf(
            createToken(TokenType.SYMBOL, "println"),
            createToken(TokenType.PUNCTUATION, "("),
            createToken(TokenType.PUNCTUATION, ")"),
            createSemicolon()
        )

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertEquals(exception.message, "Invalid expression: []")
    }

    @Test
    fun `parse function call with one parameter`() {
        val tokens = listOf(
            createToken(TokenType.SYMBOL, "println"),
            createToken(TokenType.PUNCTUATION, "("),
            createToken(TokenType.STRING, "Hello"),
            createToken(TokenType.PUNCTUATION, ")"),
            createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse multiple different statements`() {
        val tokens = listOf(
            createToken(TokenType.KEYWORD, "let"),
            createToken(TokenType.SYMBOL, "x"),
            createToken(TokenType.PUNCTUATION, ":"),
            createToken(TokenType.SYMBOL, "Number"),
            createToken(TokenType.PUNCTUATION, "="),
            createToken(TokenType.NUMBER, "5"),
            createSemicolon(),

            createToken(TokenType.SYMBOL, "y"),
            createToken(TokenType.PUNCTUATION, "="),
            createToken(TokenType.NUMBER, "10"),
            createSemicolon(),

            createToken(TokenType.SYMBOL, "println"),
            createToken(TokenType.PUNCTUATION, "("),
            createToken(TokenType.SYMBOL, "x"),
            createToken(TokenType.PUNCTUATION, ")"),
            createSemicolon()

        )

        val result = parser.parse(tokens)

        assertEquals(3, result.statements.size) // decía expected: 2, pero entiendo que expected = 3, no?
    }

    @Test
    fun `parse statements without final semicolon`() {
        val tokens = listOf(
            createToken(TokenType.KEYWORD, "let"),
            createToken(TokenType.SYMBOL, "x"),
            createToken(TokenType.PUNCTUATION, ":"),
            createToken(TokenType.SYMBOL, "Number"),
            createToken(TokenType.PUNCTUATION, "="),
            createToken(TokenType.NUMBER, "5"),
            createSemicolon(),
            createToken(TokenType.SYMBOL, "print"),
            createToken(TokenType.PUNCTUATION, "("),
            createToken(TokenType.SYMBOL, "x"),
            createToken(TokenType.PUNCTUATION, ")")
        )

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertTrue(exception.message?.contains("Expected enclosing ( ), found '(' and 'x'") == true)
    }

    @Test
    fun `throws SyntaxException for invalid syntax`() {
        val tokens = listOf(
            createToken(TokenType.KEYWORD, "let"),
            createToken(TokenType.PUNCTUATION, "="),
            createToken(TokenType.NUMBER, "5"),
            createSemicolon()
        )

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertTrue(exception.message?.contains("Invalid structure for statement") == true)
    }

    @Test
    fun `throws SyntaxException for unrecognized statement pattern`() {
        val tokens = listOf(
            createToken(TokenType.PUNCTUATION, "{"),
            createToken(TokenType.PUNCTUATION, "}"),
            createSemicolon()
        )

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertTrue(exception.message?.contains("Invalid structure for statement") == true)
    }

    @Test
    fun `throws SyntaxException for incomplete variable declaration`() {
        val tokens = listOf(
            createToken(TokenType.KEYWORD, "let"),
            createToken(TokenType.SYMBOL, "x"),
            createToken(TokenType.PUNCTUATION, ":"),
            createSemicolon()
        )

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertTrue(exception.message?.contains("Invalid structure for statement") == true)
    }

    @Test
    fun `handles single semicolon`() {
        val tokens = listOf(createSemicolon())

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertTrue(exception.message?.contains("Invalid structure for statement") == true)
    }
}
