package org.example.parser

import org.example.ast.ASTNode
import org.example.ast.statements.Condition
import org.example.common.results.Error
import org.example.common.results.Success
import org.example.parser.provider.Provider11
import org.example.token.Token
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.LinkedList

class ConditionStatementTest {

    private val tokenFactory = TokenFactory()
    private val astFactory = AstFactory()
    private val provider = Provider11()

    // ===== helpers =====
    private fun parserWith(tokens: List<Token>): Parser {
        return provider.provide(TokenBuffer(MockPSIterator(LinkedList(tokens))))
    }

    private fun tokens(vararg t: Token): List<Token> = listOf(*t)

    private fun keyword(value: String) = tokenFactory.createKeyword(value)
    private fun symbol(value: String) = tokenFactory.createSymbol(value)
    private fun punct(value: String) = tokenFactory.createPunctuation(value)
    private fun string(value: String) = tokenFactory.createString(value)
    private fun boolean(value: String) = tokenFactory.createBoolean(value)
    private fun number(value: String) = tokenFactory.createNumber(value)
    private fun equals() = tokenFactory.createEquals()
    private fun semi() = tokenFactory.createSemicolon()

    private fun parseOne(tokens: List<Token>): ASTNode {
        val parser = parserWith(tokens)
        val res = parser.parse()
        require(res is Success<*>) { "Se esperaba Success, obtuvo: $res" }
        return res.value as ASTNode
    }

    private fun parseAll(parser: Parser): List<ASTNode> {
        val nodes = mutableListOf<ASTNode>()
        while (parser.hasNext()) {
            val r = parser.parse()
            require(r is Success<*>) { "Se esperaba Success, obtuvo: $r" }
            nodes.add(r.value as ASTNode)
        }
        return nodes
    }

    private fun assertInvalid(tokens: List<Token>) {
        val parser = parserWith(tokens)
        val result = parser.parse()
        assertTrue(result is Error && result.message.contains("Error in statement: "),
            "Se esperaba SyntaxError, obtuvo: $result")
    }

    // ===== tests: casos validos =====

    @Test
    fun `build - if con bloque vacio`() {
        val node = parseOne(
            tokens(
                keyword("if"), punct("("), boolean("true"), punct(")"),
                punct("{"), punct("}")
            )
        )
        assertTrue(node is Condition, "Se esperaba Condition, obtuvo: ${node::class.simpleName}")
        assertTrue((node as Condition).elseBlock == null, "El bloque else deberia ser null")
    }

    @Test
    fun `build - if-else con bloques vacios`() {
        val node = parseOne(
            tokens(
                keyword("if"), punct("("), boolean("true"), punct(")"),
                punct("{"), punct("}"),
                keyword("else"),
                punct("{"), punct("}")
            )
        )
        assertTrue(node is Condition)
    }

    @Test
    fun `build - if con declaracion adentro`() {
        // if (true) { let x: Number; }
        val node = parseOne(
            tokens(
                keyword("if"), punct("("), boolean("true"), punct(")"),
                punct("{"),
                keyword("let"), symbol("x"), punct(":"), symbol("Number"), semi(),
                punct("}")
            )
        )
        assertTrue(node is Condition)
    }

    @Test
    fun `build - if-else con asignacion y print`() {
        // if (false) { x = "hello"; } else { println("ok"); }
        val node = parseOne(
            tokens(
                keyword("if"), punct("("), boolean("false"), punct(")"),
                punct("{"),
                symbol("x"), equals(), string("hello"), semi(),
                punct("}"),
                keyword("else"),
                punct("{"),
                symbol("println"), punct("("), string("ok"), punct(")"), semi(),
                punct("}")
            )
        )
        assertTrue(node is Condition)
    }

    @Test
    fun `build - parsea condicion y luego otra sentencia (println)`() {
        // if (true) { } ; println("done");
        val parser = parserWith(
            tokens(
                keyword("if"), punct("("), boolean("true"), punct(")"),
                punct("{"), punct("}"),
                // segunda sentencia:
                symbol("println"), punct("("), string("done"), punct(")"), semi()
            )
        )
        val nodes = parseAll(parser)
        assertEquals(2, nodes.size)
        assertTrue(nodes[0] is Condition)

        val expectedPrint = astFactory.createPrintFunction(
            org.example.ast.expressions.OptionalExpression.HasExpression(
                astFactory.createString("done")
            )
        )
        assertEquals(expectedPrint, nodes[1], "La segunda sentencia deberia ser un println(\"done\");")
    }

    // ===== tests: casos invalidos (via build) =====

    @Test
    fun `build - invalido - falta parentesis de apertura`() {
        // if true) { }
        assertInvalid(
            tokens(
                keyword("if"),
                boolean("true"), punct(")"),
                punct("{"), punct("}")
            )
        )
    }

    @Test
    fun `build - invalido - falta parentesis de cierre`() {
        // if (true { }
        assertInvalid(
            tokens(
                keyword("if"), punct("("), boolean("true"),
                punct("{"), punct("}")
            )
        )
    }

    @Test
    fun `build - invalido - falta llave de apertura despues de condicion`() {
        // if (true) }
        assertInvalid(
            tokens(
                keyword("if"), punct("("), boolean("true"), punct(")"),
                punct("}")
            )
        )
    }

    @Test
    fun `build - invalido - sin parentesis`() {
        // if { }
        assertInvalid(
            tokens(
                keyword("if"),
                punct("{"), punct("}")
            )
        )
    }

    @Test
    fun `build - invalido - condicion no booleana`() {
        // if (5) { }
        assertInvalid(
            tokens(
                keyword("if"), punct("("), number("5"), punct(")"),
                punct("{"), punct("}")
            )
        )
    }
}
