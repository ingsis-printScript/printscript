import org.example.token.TokenType
import org.example.lexer.Lexer
import org.example.lexer.provider.LexerProvider11
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LexerProvider11Test {

    private fun createLexerFromString(input: String): Lexer {
        val lines = input.lines().iterator()
        val provider = LexerProvider11()
        return provider.provide(lines)
    }

    @Test
    fun `lexer should tokenize numbers correctly`() {
        val lexer = createLexerFromString("123 456")
        val token1 = lexer.getNext()
        val token2 = lexer.getNext()

        assertEquals(TokenType.NUMBER, token1.type)
        assertEquals("123", token1.value)

        assertEquals(TokenType.NUMBER, token2.type)
        assertEquals("456", token2.value)
    }

    @Test
    fun `lexer should tokenize keywords correctly`() {
        val lexer = createLexerFromString("let const if else")
        val tokenValues = mutableListOf<String>()
        while (lexer.hasNext()) {
            tokenValues.add(lexer.getNext().value)
        }

        assertEquals(listOf("let", "const", "if", "else"), tokenValues)
        tokenValues.forEach { assertTrue(it in listOf("let", "const", "if", "else")) }
    }

    @Test
    fun `lexer should tokenize operators correctly`() {
        val lexer = createLexerFromString("+ - * /")
        val tokenValues = mutableListOf<String>()
        while (lexer.hasNext()) {
            tokenValues.add(lexer.getNext().value)
        }

        assertEquals(listOf("+", "-", "*", "/"), tokenValues)
    }

    @Test
    fun `lexer should tokenize punctuations correctly`() {
        val lexer = createLexerFromString("(:;,{})=")
        val expected = listOf("(", ":", ";", ",", "{", "}", ")", "=")
        val tokenValues = mutableListOf<String>()

        while (lexer.hasNext()) {
            tokenValues.add(lexer.getNext().value)
        }

        assertEquals(expected, tokenValues)
    }

    @Test
    fun `lexer should handle mixed input`() {
        val input = "let x = 42 + 10;"
        val lexer = createLexerFromString(input)
        val types = mutableListOf<TokenType>()
        val values = mutableListOf<String>()

        while (lexer.hasNext()) {
            val token = lexer.getNext()
            types.add(token.type)
            values.add(token.value)
        }

        assertEquals(
            listOf(
                TokenType.KEYWORD,
                TokenType.SYMBOL,
                TokenType.PUNCTUATION,
                TokenType.NUMBER,
                TokenType.OPERATOR,
                TokenType.NUMBER,
                TokenType.PUNCTUATION
            ),
            types
        )

        assertEquals(listOf("let", "x", "=", "42", "+", "10", ";"), values)
    }
}
