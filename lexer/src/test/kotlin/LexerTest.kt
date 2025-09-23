import org.example.common.exceptions.NoMoreTokensAvailableException
import org.example.lexer.Lexer
import org.example.lexer.provider.LexerProvider10
import org.example.token.Token
import org.example.token.TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LexerTest {

    private val lexerProvider = LexerProvider10()

    // Factory
    private fun createLexer(input: String): Lexer {
        val lines = listOf(input).iterator()
        return lexerProvider.provide(lines)
    }

    // Factory para inputs multi-línea
    private fun createLexerFromLines(lines: List<String>): Lexer {
        return lexerProvider.provide(lines.iterator())
    }

    // Assertion helpers
    private fun assertTokenEquals(expectedType: TokenType, expectedValue: String, actualToken: Token) {
        assertEquals(expectedType, actualToken.type, "Token type mismatch")
        assertEquals(expectedValue, actualToken.value, "Token value mismatch")
    }

    private fun assertTokenAtPosition(expectedLine: Int, expectedColumn: Int, actualToken: Token) {
        assertEquals(expectedLine, actualToken.position.line, "Token line position mismatch")
        assertEquals(expectedColumn, actualToken.position.column, "Token column position mismatch")
    }

    @Test
    fun `test lexer recognizes let keyword`() {
        // Given
        val lexer = createLexer("let")

        // When & Then
        assertTrue(lexer.hasNext(), "Lexer should have next token")

        val token = lexer.getNext()
        assertTokenEquals(TokenType.KEYWORD, "let", token)
        assertTokenAtPosition(1, 1, token)

        assertFalse(lexer.hasNext(), "Lexer should not have more tokens")
    }

    @Test
    fun `test lexer with empty input`() {
        // Given
        val lexer = createLexerFromLines(emptyList())

        // When & Then
        assertFalse(lexer.hasNext(), "Lexer should not have tokens for empty input")

        assertThrows(NoMoreTokensAvailableException::class.java) {
            lexer.getNext()
        }
    }

    @Test
    fun `test lexer with whitespace only`() {
        // Given
        val lexer = createLexer("   \t\n   ")

        // When & Then
        assertFalse(lexer.hasNext(), "Lexer should not have tokens for whitespace-only input")
    }

    @Test
    fun `test lexer with keyword surrounded by whitespace`() {
        // Given
        val lexer = createLexer("   let   ")

        // When & Then
        assertTrue(lexer.hasNext(), "Lexer should find token despite surrounding whitespace")

        val token = lexer.getNext()
        assertTokenEquals(TokenType.KEYWORD, "let", token)

        assertFalse(lexer.hasNext(), "Should only have one token")
    }

    @Test
    fun `debug whitespace trailing issue`() {
        val lexer = createLexer("   let   ")

        if (lexer.hasNext()) {
            lexer.getNext()
        }

        val hasMoreTokens = lexer.hasNext()

        if (hasMoreTokens) {
            lexer.getNext()
        }

        assertFalse(hasMoreTokens, "Should not have more tokens after 'let'")
    }

    @Test
    fun `test lexer positioning`() {
        // Given
        val lines = listOf("let", "a string")
        val lexer = createLexerFromLines(lines)

        // When & Then
        assertTrue(lexer.hasNext())
        val firstToken = lexer.getNext()
        assertTokenEquals(TokenType.KEYWORD, "let", firstToken)
        assertTokenAtPosition(1, 1, firstToken)

        assertTrue(lexer.hasNext())
        val secondToken = lexer.getNext()
        assertTokenEquals(TokenType.SYMBOL, "a", secondToken)
        assertTokenAtPosition(2, 1, secondToken)

        assertTrue(lexer.hasNext())
        val thirdToken = lexer.getNext()
        assertTokenEquals(TokenType.SYMBOL, "string", thirdToken)
        assertTokenAtPosition(2, 3, thirdToken)

        assertFalse(lexer.hasNext())
    }

    @Test
    fun `test lexer with empty lines`() {
        // Given
        val lines = listOf("", "let", "", "3", "")
        val lexer = createLexerFromLines(lines)

        // When & Then
        assertTrue(lexer.hasNext())
        assertTokenEquals(TokenType.KEYWORD, "let", lexer.getNext())

        assertTrue(lexer.hasNext())
        assertTokenEquals(TokenType.NUMBER, "3", lexer.getNext())

        assertFalse(lexer.hasNext())
    }

    @Test
    fun `test lexer returns unknown for unsupported character`() {
        // Given
        val lexer = createLexer("@invalid")

        // When & Then
        assertTrue(lexer.hasNext())

        assertEquals(TokenType.UNKNOWN, lexer.getNext().type)
    }

    @Test
    fun `test lexer with jump whitespace configuration`() {
        // Given
        val lexer = createLexer("let\na")

        // When & Then
        assertTrue(lexer.hasNext())
        assertTokenEquals(TokenType.KEYWORD, "let", lexer.getNext())

        assertTrue(lexer.hasNext())
        assertTokenEquals(TokenType.SYMBOL, "a", lexer.getNext())

        assertFalse(lexer.hasNext())
    }

    @Test
    fun `test complex PrintScript statement with arithmetic expression`() {
        // Given - "a4 =(2+2)/2;"
        val lexer = createLexer("a4:number =(2+2.0) /2;")

        // When & Then - Verify token sequence
        val expectedTokens = listOf(
            TokenType.SYMBOL to "a4",
            TokenType.PUNCTUATION to ":",
            TokenType.SYMBOL to "number",
            TokenType.PUNCTUATION to "=",
            TokenType.PUNCTUATION to "(",
            TokenType.NUMBER to "2",
            TokenType.OPERATOR to "+",
            TokenType.NUMBER to "2.0",
            TokenType.PUNCTUATION to ")",
            TokenType.OPERATOR to "/",
            TokenType.NUMBER to "2",
            TokenType.PUNCTUATION to ";"
        )

        expectedTokens.forEachIndexed { index, (expectedType, expectedValue) ->
            assertTrue(lexer.hasNext(), "Should have token at position $index: $expectedValue")

            val token = lexer.getNext()
            assertTokenEquals(expectedType, expectedValue, token)
        }

        assertFalse(lexer.hasNext(), "Should not have more tokens after complete statement")
    }

    @Test
    fun `test PrintScript println statement with string concatenation`() {
        // Given - "println(\"hello, \" + 'world!')"
        val lexer = createLexer("println(\"hello, \" + 'world!')")

        // When & Then - Verify token sequence
        val expectedTokens = listOf(
            TokenType.SYMBOL to "println",
            TokenType.PUNCTUATION to "(",
            TokenType.STRING to "\"hello, \"",
            TokenType.OPERATOR to "+",
            TokenType.STRING to "'world!'",
            TokenType.PUNCTUATION to ")"
        )

        expectedTokens.forEachIndexed { index, (expectedType, expectedValue) ->
            assertTrue(lexer.hasNext(), "Should have token at position $index: $expectedValue")

            val token = lexer.getNext()
            assertTokenEquals(expectedType, expectedValue, token)
        }

        assertFalse(lexer.hasNext(), "Should not have more tokens after println statement")
    }
}
