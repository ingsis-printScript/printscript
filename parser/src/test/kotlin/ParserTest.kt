package org.example.parser

import org.example.ast.expressions.OptionalExpression
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.parser.exceptions.SyntaxException
import org.example.parser.parsers.function.PrintParser
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

    // todo cambiar throws / analizar throws es lo mejor (medio raro)
    private lateinit var parser: Parser
    private val tokenFactory = TokenFactory()
    private val AstFactory = AstFactory()

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

        val exception = assertThrows<SyntaxException> { parser.parse(emptyTokens) }

        assertEquals(exception.message, "Empty token list")
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

        val expectedSymbol = AstFactory.createSymbol("x")
        val expected = AstFactory.createVariableDeclarator(expectedSymbol, Type.NUMBER)


        assertEquals(expected, result)
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
            tokenFactory.createOperator("+"),
            tokenFactory.createNumber("3"),
            tokenFactory.createSemicolon()
        )

        val result = parser.parse(tokens)

        val expectedSymbol = AstFactory.createSymbol("x")
        val expression = AstFactory.createBinaryExpression(
            AstFactory.createNumber("5"),
            Operator.ADD,
            AstFactory.createNumber("3")
        )
        val expected = AstFactory.createVariableDeclarator(expectedSymbol, Type.NUMBER, OptionalExpression.HasExpression(expression))

        assertEquals(expected, result)
    }

    @Test
    fun `parse variable declaration with string value`() {
        val tokens = listOf(
            tokenFactory.createKeyword("let"),
            tokenFactory.createSymbol("x"),
            tokenFactory.createPunctuation(":"),
            tokenFactory.createSymbol("String"),
            tokenFactory.createEquals(),
            tokenFactory.createString("John"),
            tokenFactory.createSemicolon()
        )

        val result = parser.parse(tokens)

        val expected = AstFactory.createVariableDeclarator(
            AstFactory.createSymbol("x"),
            Type.STRING,
            OptionalExpression.HasExpression(AstFactory.createString("John"))
        )

        assertEquals(expected, result)
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

        val expected = AstFactory.createVariableAssigment(
            AstFactory.createSymbol("x"),
            OptionalExpression.HasExpression(AstFactory.createNumber("10"))
        )

        assertEquals(expected, result)
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

        val expected = AstFactory.createVariableAssigment(
            AstFactory.createSymbol("y"),
            OptionalExpression.HasExpression(AstFactory.createSymbol("x"))
        )

        assertEquals(expected, result)
    }

    @Test
    fun `parse function call without parameters`() {
        val tokens = listOf(
            tokenFactory.createSymbol("println"),
            tokenFactory.createPunctuation("["),
            tokenFactory.createPunctuation("]"),
            tokenFactory.createSemicolon()
        )

        assertThrows<SyntaxException> { parser.parse(tokens) }
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

        val expected = AstFactory.createPrintFunction(
            OptionalExpression.HasExpression(AstFactory.createString("Hello"))
        )

        assertEquals(expected, result)
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
