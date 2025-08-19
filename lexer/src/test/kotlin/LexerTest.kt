package org.example.lexer

import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.common.tokens.constructors.NumberTokenConstructor
import org.example.common.tokens.detectors.*
import org.example.lexer.exceptions.NoMoreTokensAvailableException
import org.example.lexer.exceptions.UnsupportedCharacterException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*


class LexerTest {

    // Config
    private data class LexerConfig(
        val constructors: List<TokenConstructor> = emptyList(),
        val keywords: KeywordTokenConstructor = KeywordTokenConstructor(),
        val whiteSpaces: List<Char> = listOf(' ', '\t', '\n')
    )

    // Factory
    private fun createLexer(input: String, config: LexerConfig = LexerConfig()): Lexer {
        val lines = listOf(input).iterator()
        return Lexer(
            reader = lines,
            constructors = config.constructors,
            keywords = config.keywords,
            whiteSpaces = config.whiteSpaces
        )
    }

    // Factory para inputs multi-línea
    private fun createLexerFromLines(lines: List<String>, config: LexerConfig = LexerConfig()): Lexer {
        return Lexer(
            reader = lines.iterator(),
            constructors = config.constructors,
            keywords = config.keywords,
            whiteSpaces = config.whiteSpaces
        )
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

    // Test data builders para casos comunes
    private fun buildDefaultConfig() = LexerConfig()

    private fun buildConfigWithConstructors(vararg constructors: TokenConstructor) =
        LexerConfig(constructors = constructors.toList())

    @Test
    fun `test lexer recognizes let keyword`() {
        // Given
        val lexer = createLexer("let")

        // When & Then
        assertTrue(lexer.hasNext(), "Lexer should have next token")

        val token = lexer.getNext()
        assertTokenEquals(TokenType.KEYWORD, "let", token)
        assertTokenAtPosition(1, 0, token)

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

        println("=== DEBUGGING WHITESPACE ISSUE ===")

        // Primera verificación
        println("1. First hasNext(): ${lexer.hasNext()}")

        // Obtener primer token
        if (lexer.hasNext()) {
            val token = lexer.getNext()
            println("2. Got token: '${token.value}' (${token.type})")
        }

        // Segunda verificación (aquí falla)
        val hasMoreTokens = lexer.hasNext()
        println("3. Second hasNext(): $hasMoreTokens")

        if (hasMoreTokens) {
            try {
                val nextToken = lexer.getNext()
                println("4. Unexpected token: '${nextToken.value}' (${nextToken.type})")
            } catch (e: Exception) {
                println("4. Exception getting next token: ${e.message}")
            }
        }

        // Lo que esperamos
        assertFalse(hasMoreTokens, "Should not have more tokens after 'let'")
    }

    @Test
    fun `test lexer with multiple lines`() {
        // Given
        val lines = listOf("let", "const")
        val lexer = createLexerFromLines(lines)

        // When & Then
        assertTrue(lexer.hasNext())
        val firstToken = lexer.getNext()
        assertTokenEquals(TokenType.KEYWORD, "let", firstToken)
        assertTokenAtPosition(1, 0, firstToken)

        assertTrue(lexer.hasNext())
        val secondToken = lexer.getNext()
        assertTokenEquals(TokenType.KEYWORD, "const", secondToken)
        assertTokenAtPosition(2, 0, secondToken)

        assertFalse(lexer.hasNext())
    }

    @Test
    fun `test lexer with empty lines`() {
        // Given
        val lines = listOf("", "let", "", "const", "")
        val lexer = createLexerFromLines(lines)

        // When & Then
        assertTrue(lexer.hasNext())
        assertTokenEquals(TokenType.KEYWORD, "let", lexer.getNext())

        assertTrue(lexer.hasNext())
        assertTokenEquals(TokenType.KEYWORD, "const", lexer.getNext())

        assertFalse(lexer.hasNext())
    }

    @Test
    fun `test lexer throws exception for unsupported character`() {
        // Given
        val lexer = createLexer("@invalid")

        // When & Then
        assertTrue(lexer.hasNext())

        assertThrows(UnsupportedCharacterException::class.java) {
            lexer.getNext()
        }
    }

    @Test
    fun `test lexer with custom whitespace configuration`() {
        // Given
        val customConfig = LexerConfig(whiteSpaces = listOf(' ', '_'))
        val lexer = createLexer("let_const", customConfig)

        // When & Then
        assertTrue(lexer.hasNext())
        assertTokenEquals(TokenType.KEYWORD, "let", lexer.getNext())

        assertTrue(lexer.hasNext())
        assertTokenEquals(TokenType.KEYWORD, "const", lexer.getNext())

        assertFalse(lexer.hasNext())
    }

    @Test
    fun `test complex PrintScript statement with arithmetic expression`() {
        // Given - "let a: number=(2+2)/2;"
        val arithmeticConstructors = listOf(
            NumberTokenConstructor(),
            SymbolTokenConstructor(),
            OperatorTokenConstructor(),
            PunctuationTokenConstructor()
        )
        val config = buildConfigWithConstructors(*arithmeticConstructors.toTypedArray())
        val lexer = createLexer("let a:number=(2+2) /2;", config)

        // When & Then - Verify token sequence
        val expectedTokens = listOf(
            TokenType.KEYWORD to "let",
            TokenType.SYMBOL to "a",
            TokenType.PUNCTUATION to ":",
            TokenType.KEYWORD to "number",
            TokenType.PUNCTUATION to "=",
            TokenType.PUNCTUATION to "(",
            TokenType.NUMBER to "2",
            TokenType.OPERATOR to "+",
            TokenType.NUMBER to "2",
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
        val printlnConstructors = listOf(
            SymbolTokenConstructor(),
            StringTokenConstructor(),
            OperatorTokenConstructor(),
            PunctuationTokenConstructor()
        )
        val config = buildConfigWithConstructors(*printlnConstructors.toTypedArray())
        val lexer = createLexer("println(\"hello, \" + 'world!')", config)

        // When & Then - Verify token sequence
        val expectedTokens = listOf(
            TokenType.KEYWORD to "println",
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