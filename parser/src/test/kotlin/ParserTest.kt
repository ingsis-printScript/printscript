package org.example.parser

import org.example.common.Position
import org.example.common.tokens.Token
import org.example.common.enums.TokenType
import org.example.parser.exceptions.SyntaxException
import org.example.parser.parsers.functionparsers.PrintParser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.example.token.Token
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
    private val tokenFactory = TokenFactory()

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
            tokenFactory.createKeyword( "let"),
            tokenFactory.createSymbol("x"),
            tokenFactory.createPunctuation(":"),
            tokenFactory.createSymbol("number"),
            tokenFactory.createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse variable declaration with assignment`() {
        val tokens = listOf(
            tokenFactory.createKeyword("let"),
            tokenFactory.createSymbol("x"),
            tokenFactory.createPunctuation(":"),
            tokenFactory.createSymbol("number"),
            tokenFactory.createEquals(),
            tokenFactory.createNumber("5"),
            // meter expresion
            tokenFactory.createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse variable declaration with string value`() {
        val tokens = listOf(
            tokenFactory.createKeyword("let"),
            tokenFactory.createSymbol("name"),
            tokenFactory.createPunctuation(":"),
            tokenFactory.createSymbol("String"),
            tokenFactory.createEquals(),
            tokenFactory.createString("John"),
            tokenFactory.createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse simple variable assignment`() {
        val tokens = listOf(
            tokenFactory.createSymbol("x"),
            tokenFactory.createEquals(),
            tokenFactory.createNumber("10"),
            tokenFactory.createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse variable assignment with another variable`() {
        val tokens = listOf(
            tokenFactory.createSymbol("y"),
            tokenFactory.createEquals(),
            tokenFactory.createSymbol("x"),
            tokenFactory.createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse function call without parameters`() {
        val tokens = listOf(
            tokenFactory.createSymbol("println"),
            tokenFactory.createPunctuation("("),
            tokenFactory.createPunctuation(")"),
            tokenFactory.createSemicolon()
        )

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertEquals(exception.message, "Invalid expression: []")
    }

    @Test
    fun `parse function call with one parameter`() {
        val tokens = listOf(
            tokenFactory.createSymbol("println"),
            tokenFactory.createPunctuation("("),
            tokenFactory.createString("Hello"),
            tokenFactory.createPunctuation(")"),
            tokenFactory.createSemicolon()
        )

        val result = parser.parse(tokens)

        assertEquals(1, result.statements.size)
    }

    @Test
    fun `parse multiple different statements`() {
        val tokens = listOf(
            tokenFactory.createKeyword("let"),
            tokenFactory.createSymbol("x"),
            tokenFactory.createPunctuation(":"),
            tokenFactory.createSymbol("Number"),
            tokenFactory.createEquals(),
            tokenFactory.createNumber("5"),
            tokenFactory.createSemicolon(),

            tokenFactory.createSymbol("y"),
            tokenFactory.createEquals(),
            tokenFactory.createNumber("10"),
            tokenFactory.createSemicolon(),

            tokenFactory.createSymbol("println"),
            tokenFactory.createPunctuation("("),
            tokenFactory.createSymbol("x"),
            tokenFactory.createPunctuation(")"),
            tokenFactory.createSemicolon()

        )

        val result = parser.parse(tokens)

        assertEquals(3, result.statements.size)
    }

    @Test
    fun `parse statements without final semicolon`() {
        val tokens = listOf(
            tokenFactory.createKeyword("let"),
            tokenFactory.createSymbol("x"),
            tokenFactory.createPunctuation(":"),
            tokenFactory.createSymbol("Number"),
            tokenFactory.createEquals(),
            tokenFactory.createNumber("5"),
            tokenFactory.createSemicolon(),
            tokenFactory.createSymbol("print"),
            tokenFactory.createPunctuation("("),
            tokenFactory.createSymbol("x"),
            tokenFactory.createPunctuation(")")
        )

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertTrue(exception.message?.contains("Expected enclosing ( ), found '(' and 'x'") == true)
    }

    @Test
    fun `throws SyntaxException for invalid syntax`() {
        val tokens = listOf(
            tokenFactory.createKeyword("let"),
            tokenFactory.createEquals(),
            tokenFactory.createNumber("5"),
            tokenFactory.createSemicolon()
        )

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertTrue(exception.message?.contains("Invalid structure for statement") == true)
    }

    @Test
    fun `throws SyntaxException for unrecognized statement pattern`() {
        val tokens = listOf(
            tokenFactory.createPunctuation("{"),
            tokenFactory.createPunctuation("}"),
            tokenFactory.createSemicolon()
        )

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertTrue(exception.message?.contains("Invalid structure for statement") == true)
    }

    @Test
    fun `throws SyntaxException for incomplete variable declaration`() {
        val tokens = listOf(
            tokenFactory.createKeyword("let"),
            tokenFactory.createSymbol("x"),
            tokenFactory.createPunctuation(":"),
            tokenFactory.createSemicolon()
        )

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertTrue(exception.message?.contains("Invalid structure for statement") == true)
    }

    @Test
    fun `handles single semicolon`() {
        val tokens = listOf(tokenFactory.createSemicolon())

        val exception = assertThrows<SyntaxException> { parser.parse(tokens) }
        assertTrue(exception.message?.contains("Invalid structure for statement") == true)
    }
}
