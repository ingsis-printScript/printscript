import org.example.parser.AnalysisOutcome
import org.example.parser.Parser
import org.example.parser.TokenBuffer
import org.example.parser.parsers.StatementParser
import org.example.parser.provider.Provider11
import org.example.token.Token
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.LinkedList

class ConditionParserValidationTest {

    private val tokenFactory = TokenFactory()

    // ---------------------------
    // Helpers
    // ---------------------------

    private fun tokens(vararg t: Token): List<Token> = listOf(*t)

    private fun keyword(value: String) = tokenFactory.createKeyword(value)
    private fun symbol(value: String) = tokenFactory.createSymbol(value)
    private fun punct(value: String) = tokenFactory.createPunctuation(value)
    private fun string(value: String) = tokenFactory.createString(value)
    private fun boolean(value: String) = tokenFactory.createBoolean(value)
    private fun semi() = tokenFactory.createSemicolon()

    private fun newParser(tokens: List<Token>): Triple<Parser, TokenBuffer, List<StatementParser>> {
        val buf = TokenBuffer(MockPSIterator(LinkedList(tokens)))

        val provider = Provider11()
        val parser = provider.provide(buf)
        return Triple(parser, buf, parser.getParsers())
    }

    private fun assertValidByAnalyze(tokens: List<Token>) {
        val (parser, buf, parsers) = newParser(tokens)
        val outcome = parser.analyzeStatement(buf, parsers)
        assertTrue(outcome is AnalysisOutcome.Success, "Se esperaba Success, obtuvo: $outcome")
        val consumed = (outcome as AnalysisOutcome.Success).result.consumed
        assertEquals(tokens.size, consumed.size, "La cantidad de tokens consumidos no coincide")
    }

    private fun assertInvalidByAnalyze(tokens: List<Token>) {
        val (parser, buf, parsers) = newParser(tokens)
        val outcome = parser.analyzeStatement(buf, parsers)
        assertTrue(outcome is AnalysisOutcome.Error, "Se esperaba Error, obtuvo: $outcome")
    }

    // ---------------------------
    // Tests de VALIDACIÓN
    // ---------------------------

    @Test
    fun `validate if with empty block`() {
        assertValidByAnalyze(
            tokens(
                keyword("if"), punct("("), boolean("true"), punct(")"),
                punct("{"), punct("}")
            )
        )
    }

    @Test
    fun `validate if-else with empty blocks`() {
        assertValidByAnalyze(
            tokens(
                keyword("if"), punct("("), boolean("true"), punct(")"),
                punct("{"), punct("}"),
                keyword("else"),
                punct("{"), punct("}")
            )
        )
    }

    @Test
    fun `validate if with non-empty block (declaration inside)`() {
        // if (true) { let x:Number; }
        assertValidByAnalyze(
            tokens(
                keyword("if"), punct("("), boolean("true"), punct(")"),
                punct("{"),
                keyword("let"), symbol("x"), punct(":"), symbol("Number"), semi(),
                punct("}")
            )
        )
    }

    @Test
    fun `validate if-else with non-empty blocks (assign + print)`() {
        // if (false) { x = "hello"; } else { println("ok"); }
        assertValidByAnalyze(
            tokens(
                keyword("if"), punct("("), boolean("false"), punct(")"),
                punct("{"),
                symbol("x"), punct("="), string("hello"), semi(),
                punct("}"),
                keyword("else"),
                punct("{"),
                symbol("println"), punct("("), string("ok"), punct(")"), semi(),
                punct("}")
            )
        )
    }

    @Test
    fun `validate if-else with non-empty blocks (decl + print)`() {
        // if (false) { let x: Number; } else { println("ok"); }
        assertValidByAnalyze(
            tokens(
                keyword("if"), punct("("), boolean("false"), punct(")"),
                punct("{"),
                keyword("let"), symbol("x"), punct(":"), symbol("Number"), semi(),
                punct("}"),
                keyword("else"),
                punct("{"),
                symbol("println"), punct("("), string("ok"), punct(")"), semi(),
                punct("}")
            )
        )
    }

    @Test
    fun `valid - else without block`() {
        // if (false) { } else { println("x");}
        assertValidByAnalyze(
            tokens(
                keyword("if"), punct("("), boolean("false"), punct(")"),
                punct("{"), punct("}"),
                keyword("else"), punct("{"),
                symbol("println"), punct("("), string("x"), punct(")"), semi(),
                punct("}")
            )
        )
    }

    // ---------------------------
    // Casos inválidos (solo patterns)
    // ---------------------------

    @Test
    fun `invalid - missing opening parenthesis`() {
        // if false) { }
        assertInvalidByAnalyze(
            tokens(
                keyword("if"),
                boolean("false"), punct(")"),
                punct("{"), punct("}")
            )
        )
    }

    @Test
    fun `invalid - missing closing parenthesis`() {
        // if (true { }
        assertInvalidByAnalyze(
            tokens(
                keyword("if"), punct("("), boolean("true"),
                punct("{"), punct("}")
            )
        )
    }

    @Test
    fun `invalid - missing opening brace after condition`() {
        // if (true) }
        assertInvalidByAnalyze(
            tokens(
                keyword("if"), punct("("), boolean("true"), punct(")"),
                punct("}")
            )
        )
    }

    @Test
    fun `invalid - missing parentheses entirely`() {
        // if { }
        assertInvalidByAnalyze(
            tokens(
                keyword("if"),
                punct("{"), punct("}")
            )
        )
    }
}
