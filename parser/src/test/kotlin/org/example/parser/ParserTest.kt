package org.example.parser

import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.common.results.Error
import org.example.common.results.Success
import org.example.parser.provider.ParserProvider10
import org.example.token.Token
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class ParserTest {

    private val tokenFactory = TokenFactory()
    private val astFactory = AstFactory()
    private val provider = ParserProvider10()

    private fun parserWith(tokens: List<Token>): Parser {
        return provider.provide(TokenBuffer(MockPSIterator(LinkedList(tokens))))
    }

    // helpers ================================

    private fun tokens(vararg t: Token): List<Token> = listOf(*t)

    private fun keyword(value: String) = tokenFactory.createKeyword(value)
    private fun symbol(value: String) = tokenFactory.createSymbol(value)
    private fun punct(value: String) = tokenFactory.createPunctuation(value)
    private fun number(value: String) = tokenFactory.createNumber(value)
    private fun string(value: String) = tokenFactory.createString(value)
    private fun equals() = tokenFactory.createEquals()
    private fun op(value: String) = tokenFactory.createOperator(value)
    private fun semi() = tokenFactory.createSemicolon()

    private fun parseAll(parser: Parser): List<ASTNode> {
        val nodes = mutableListOf<ASTNode>()
        while (parser.hasNext()) {
            val result = parser.parse()
            require(result is Success<*>) { "Expected success but got $result" }
            nodes.add(result.value as ASTNode)
        }
        return nodes
    }

    private fun assertInvalid(tokens: List<Token>) {
        val parser = parserWith(tokens)
        val result = parser.parse()
        assertTrue(result is Error && result.message.contains("Error in statement: "))
    }

    private inline fun <reified T> assertParsed(tokens: List<Token>, expected: T) {
        val parser = parserWith(tokens)
        val result = parser.parse()
        assertTrue(result is Success<*> && result.value == expected)
    }

    // tests ================================

    @Test
    fun `parse empty token list returns empty program`() {
        val parser = parserWith(emptyList())
        assertEquals(Error("No tokens to parse"), parser.parse())
    }

    @Test
    fun `parse variable declaration without assignment`() {
        val expected = astFactory.createVariableDeclarator(
            astFactory.createSymbol("x"),
            Type.NUMBER
        )
        assertParsed(
            tokens(keyword("let"), symbol("x"), punct(":"), symbol("number"), semi()),
            expected
        )
    }

    @Test
    fun `parse variable declaration with assignment`() {
        val expression = astFactory.createBinaryExpression(
            astFactory.createNumber("5"),
            Operator.ADD,
            astFactory.createNumber("3")
        )
        val expected = astFactory.createVariableDeclarator(
            astFactory.createSymbol("x"),
            Type.NUMBER,
            OptionalExpression.HasExpression(expression)
        )
        assertParsed(
            tokens(
                keyword("let"), symbol("x"), punct(":"), symbol("number"),
                equals(), number("5"), op("+"), number("3"), semi()
            ),
            expected
        )
    }

    @Test
    fun `parse variable declaration with string value`() {
        val expected = astFactory.createVariableDeclarator(
            astFactory.createSymbol("x"),
            Type.STRING,
            OptionalExpression.HasExpression(astFactory.createString("John"))
        )
        assertParsed(
            tokens(
                keyword("let"),
                symbol("x"),
                punct(":"),
                symbol("string"),
                equals(),
                string("John"),
                semi()
            ),
            expected
        )
    }

    @Test
    fun `parse simple variable assignment`() {
        val expected = astFactory.createVariableAssigment(
            astFactory.createSymbol("x"),
            OptionalExpression.HasExpression(astFactory.createNumber("10"))
        )
        assertParsed(
            tokens(symbol("x"), equals(), number("10"), semi()),
            expected
        )
    }

    @Test
    fun `parse variable assignment with another variable`() {
        val expected = getVariableAssignment()
        assertParsed(
            tokens(symbol("y"), equals(), symbol("x"), semi()),
            expected
        )
    }

    @Test
    fun `parse function call without parameters`() {
        val parser = parserWith(
            tokens(symbol("println"), punct("["), punct("]"), semi())
        )
        val result = parser.parse()
        assertTrue(result is Error)
    }

    @Test
    fun `parse function call with one parameter`() {
        val expected = astFactory.createPrintFunction(
            OptionalExpression.HasExpression(astFactory.createString("Hello"))
        )
        assertParsed(
            tokens(symbol("println"), punct("("), string("Hello"), punct(")"), semi()),
            expected
        )
    }

    @Test
    fun `throws SyntaxException for invalid syntax`() {
        assertInvalid(tokens(keyword("let"), equals(), number("5"), semi()))
    }

    @Test
    fun `throws SyntaxException for unrecognized statement pattern`() {
        assertInvalid(tokens(punct("{"), punct("}"), semi()))
    }

    @Test
    fun `throws SyntaxException for incomplete variable declaration`() {
        assertInvalid(tokens(keyword("let"), symbol("x"), punct(":"), semi()))
    }

    @Test
    fun `handles single semicolon`() {
        assertInvalid(tokens(semi()))
    }

    @Test
    fun `parse multiple statements in sequence`() {
        val nodes = parseAll(
            parserWith(
                tokens(
                    // let x:number = 5;
                    keyword("let"), symbol("x"), punct(":"), symbol("number"),
                    equals(), number("5"), semi(),
                    // y = x;
                    symbol("y"), equals(), symbol("x"), semi(),
                    // println("done");
                    symbol("println"), punct("("), string("done"), punct(")"), semi()
                )
            )
        )

        assertEquals(3, nodes.size)

        assertEquals(getVariableDeclarator(), nodes[0])

        assertEquals(getVariableAssignment(), nodes[1])

        assertEquals(getPrintFunction(), nodes[2])
    }

    private fun getPrintFunction() = astFactory.createPrintFunction(
        OptionalExpression.HasExpression(astFactory.createString("done"))
    )

    private fun getVariableAssignment() = astFactory.createVariableAssigment(
        astFactory.createSymbol("y"),
        OptionalExpression.HasExpression(astFactory.createSymbol("x"))
    )

    private fun getVariableDeclarator() = astFactory.createVariableDeclarator(
        astFactory.createSymbol("x"),
        Type.NUMBER,
        OptionalExpression.HasExpression(astFactory.createNumber("5"))
    )
}
