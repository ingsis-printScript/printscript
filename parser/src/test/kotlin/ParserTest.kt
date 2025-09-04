package org.example.parser

import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.common.results.Error
import org.example.common.results.Success
import org.example.parser.parsers.function.PrintParser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.example.token.Token

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ParserTest {

    private lateinit var parser: Parser
    private val tokenFactory = TokenFactory()
    private val astFactory = AstFactory()

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

        assertEquals(Error("Empty token list"), parser.parse(emptyTokens))
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

        val expectedSymbol = astFactory.createSymbol("x")
        val expected = astFactory.createVariableDeclarator(expectedSymbol, Type.NUMBER)

        assertSuccess<ASTNode>(result, expected)
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

        val expectedSymbol = astFactory.createSymbol("x")
        val expression = astFactory.createBinaryExpression(
            astFactory.createNumber("5"),
            Operator.ADD,
            astFactory.createNumber("3")
        )
        val expected = astFactory
            .createVariableDeclarator(
                expectedSymbol,
                Type.NUMBER,
                OptionalExpression.HasExpression(expression)
            )

        assertSuccess<ASTNode>(result, expected)
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

        val expected = astFactory.createVariableDeclarator(
            astFactory.createSymbol("x"),
            Type.STRING,
            OptionalExpression.HasExpression(astFactory.createString("John"))
        )

        assertSuccess<ASTNode>(result, expected)
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

        val expected = astFactory.createVariableAssigment(
            astFactory.createSymbol("x"),
            OptionalExpression.HasExpression(astFactory.createNumber("10"))
        )

        assertSuccess<ASTNode>(result, expected)
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

        val expected = astFactory.createVariableAssigment(
            astFactory.createSymbol("y"),
            OptionalExpression.HasExpression(astFactory.createSymbol("x"))
        )

        assertSuccess<ASTNode>(result, expected)
    }

    @Test
    fun `parse function call without parameters`() {
        val tokens = listOf(
            tokenFactory.createSymbol("println"),
            tokenFactory.createPunctuation("["),
            tokenFactory.createPunctuation("]"),
            tokenFactory.createSemicolon()
        )

        assertTrue(parser.parse(tokens) is Error)
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

        val expected = astFactory.createPrintFunction(
            OptionalExpression.HasExpression(astFactory.createString("Hello"))
        )

        assertSuccess<ASTNode>(result, expected)
    }


    @Test
    fun `throws SyntaxException for invalid syntax`() {
        val tokens = listOf(
            tokenFactory.createKeyword("let"),
            tokenFactory.createEquals(),
            tokenFactory.createNumber("5"),
            tokenFactory.createSemicolon()
        )

        assertInvalidStructure(tokens)
    }

    @Test
    fun `throws SyntaxException for unrecognized statement pattern`() {
        val tokens = listOf(
            tokenFactory.createPunctuation("{"),
            tokenFactory.createPunctuation("}"),
            tokenFactory.createSemicolon()
        )

        assertInvalidStructure(tokens)
    }

    @Test
    fun `throws SyntaxException for incomplete variable declaration`() {
        val tokens = listOf(
            tokenFactory.createKeyword("let"),
            tokenFactory.createSymbol("x"),
            tokenFactory.createPunctuation(":"),
            tokenFactory.createSemicolon()
        )

        assertInvalidStructure(tokens)
    }

    @Test
    fun `handles single semicolon`() {
        val tokens = listOf(tokenFactory.createSemicolon())

        assertInvalidStructure(tokens)
    }

    private fun assertInvalidStructure(tokens: List<Token>) {
        val result = parser.parse(tokens)
        assertTrue(
            result is Error && result.message.contains("Invalid structure for statement")
        )
    }

    private inline fun <reified T> assertSuccess(result: Any, expected: T) {
        assertTrue(
                result is Success<*>
                && result.value is T
                && result.value == expected
        )
    }
}
