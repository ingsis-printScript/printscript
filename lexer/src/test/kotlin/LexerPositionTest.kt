import org.example.token.TokenType
import org.example.lexer.Lexer
import org.example.lexer.provider.LexerProvider10
import org.example.token.Token
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class LexerPositionTest {

    private val lexerProvider = LexerProvider10()

    private fun createLexerFromLines(lines: List<String>): Lexer {
        return lexerProvider.provide(lines.iterator())
    }

    private fun createLexerFromFile(filePath: Path): Lexer {
        val lines = Files.readAllLines(filePath)
        return lexerProvider.provide(lines.iterator())
    }

    private fun assertToken(
        expectedType: TokenType,
        expectedValue: String,
        expectedLine: Int,
        expectedColumn: Int,
        actualToken: Token
    ) {
        assertEquals(expectedType, actualToken.type, "Token type mismatch")
        assertEquals(expectedValue, actualToken.value, "Token value mismatch")
        assertEquals(
            expectedLine,
            actualToken.position.line,
            "Token line position mismatch for '${actualToken.value}'"
        )
        assertEquals(
            expectedColumn,
            actualToken.position.column,
            "Token column position mismatch for '${actualToken.value}'"
        )
    }

    private fun getAllTokens(lexer: Lexer): List<Token> {
        val tokens = mutableListOf<Token>()
        while (lexer.hasNext()) { tokens.add(lexer.getNext()) }
        return tokens
    }

    @Test
    fun `test simple single line token positioning`() {
        // "let x: number = 42;"
        val lexer = createLexerFromLines(listOf("let x: number = 42;"))
        val tokens = getAllTokens(lexer)

        assertEquals(7, tokens.size)
        assertToken(TokenType.KEYWORD, "let", 1, 1, tokens[0])
        assertToken(TokenType.SYMBOL, "x", 1, 5, tokens[1])
        assertToken(TokenType.PUNCTUATION, ":", 1, 6, tokens[2])
        assertToken(TokenType.SYMBOL, "number", 1, 8, tokens[3])
        assertToken(TokenType.PUNCTUATION, "=", 1, 15, tokens[4])
        assertToken(TokenType.NUMBER, "42", 1, 17, tokens[5])
        assertToken(TokenType.PUNCTUATION, ";", 1, 19, tokens[6])
    }

    @Test
    fun `test multi-line token positioning with whitespace`() {
        val lines = listOf(
            "let name: string;", // Line 1
            "", // Line 2 (empty)
            "  name = \"Hello\";", // Line 3 (with leading spaces)
            "println(name);" // Line 4
        )
        val tokens = getAllTokens(createLexerFromLines(lines))

        val keyTokens = mapOf(
            "let" to Triple(TokenType.KEYWORD, 1, 1),
            "name" to Triple(TokenType.SYMBOL, 1, 5),
            "\"Hello\"" to Triple(TokenType.STRING, 3, 10),
            "println" to Triple(TokenType.SYMBOL, 4, 1)
        )

        keyTokens.forEach { (value, expected) ->
            val token = tokens.first { it.value == value }
            assertToken(expected.first, value, expected.second, expected.third, token)
        }

        val nameAssignment = tokens.filter { it.value == "name" }[1]
        assertToken(TokenType.SYMBOL, "name", 3, 3, nameAssignment)
    }

    @Suppress("LongMethod")
    @Test
    fun `test extensive PrintScript program from file`() {
        val testFile = Path.of("src/test/kotlin/resources/test_program.ps")
        assertTrue(Files.exists(testFile), "Test file should exist: ${testFile.toAbsolutePath()}")

        val tokens = getAllTokens(createLexerFromFile(testFile))

        assertTrue(tokens.isNotEmpty(), "Should have extracted tokens from file")

        val expectedPositions = mapOf(
            "let" to listOf(Triple(TokenType.KEYWORD, 1, 1)),
            "name" to listOf(Triple(TokenType.SYMBOL, 1, 5)),
            "\"Joe\"" to listOf(Triple(TokenType.STRING, 1, 20)),
            "lastName" to listOf(Triple(TokenType.SYMBOL, 2, 5)),
            "\"Doe\"" to listOf(Triple(TokenType.STRING, 2, 24)),
            "println" to listOf(Triple(TokenType.SYMBOL, 3, 1)),
            "\" \"" to listOf(Triple(TokenType.STRING, 3, 16)),
            "12" to listOf(Triple(TokenType.NUMBER, 5, 17)),
            "/" to listOf(Triple(TokenType.OPERATOR, 7, 19)),
            "cuenta" to listOf(Triple(TokenType.SYMBOL, 10, 5)),
            "*" to listOf(Triple(TokenType.OPERATOR, 10, 23)),
            "-" to listOf(Triple(TokenType.OPERATOR, 10, 25)),
            "3.14159" to listOf(Triple(TokenType.NUMBER, 14, 6)),
            "\"Final calculation\"" to listOf(Triple(TokenType.STRING, 17, 23)),
            "\": \"" to listOf(Triple(TokenType.STRING, 18, 19))
        )

        expectedPositions.forEach { (value, expectations) ->
            val matchingTokens = tokens.filter { it.value == value }
            assertTrue(matchingTokens.isNotEmpty(), "Should find token: $value")

            expectations.forEachIndexed { index, (expectedType, expectedLine, expectedColumn) ->
                assertTrue(
                    index < matchingTokens.size,
                    "Should have enough occurrences of token: $value"
                )
                assertToken(expectedType, value, expectedLine, expectedColumn, matchingTokens[index])
            }
        }

        assertEquals(18, tokens.maxOf { it.position.line }, "Should process 18 lines")

        val tokenCounts = tokens.groupBy { it.type }.mapValues { it.value.size }

        assertTrue(
            tokenCounts.getOrDefault(TokenType.KEYWORD, 0) > 0,
            "Should have keywords"
        )
        assertTrue(
            tokenCounts.getOrDefault(TokenType.SYMBOL, 0) > 0,
            "Should have symbols"
        )
        assertTrue(
            tokenCounts.getOrDefault(TokenType.STRING, 0) > 0,
            "Should have strings"
        )
    }
}
