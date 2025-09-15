package org.example.integration

import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator
import org.example.parser.TokenBuffer
import org.example.token.Token
import java.io.InputStream
import org.example.lexer.provider.LexerProvider10 as LexerProvider10
import org.example.parser.provider.ParserProvider10 as ParserProvider10

/**
 * Utilidad completa para testing que integra el pipeline Lexer-Parser
 */
class PrintScriptTestUtils {

    // ========================================
    // EXTENSION FUNCTIONS
    // ========================================

    /**
     * Convierte un String multi-línea en Iterator<String> para el Lexer
     */
    fun String.toLineIterator(): Iterator<String> {
        return this.byteInputStream()
            .bufferedReader()
            .lineSequence()
            .iterator()
    }

    /**
     * Convierte InputStream en Iterator<String>
     */
    fun InputStream.toLineIterator(): Iterator<String> {
        return this.bufferedReader()
            .lineSequence()
            .iterator()
    }

    // ========================================
    // FACTORY METHODS
    // ========================================

    /**
     * Crea un Lexer desde código fuente como String
     */
    fun createLexer(sourceCode: String, version: String = "1.0"): PrintScriptIterator<Token> {
        val lineIterator = sourceCode.toLineIterator()
        return when (version) {
            "1.0" -> {
                val provider = LexerProvider10()
                provider.provide(lineIterator)
            }
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }

    /**
     * Crea un Parser desde un Lexer
     */
    private fun createParser(lexer: PrintScriptIterator<Token>, version: String = "1.0"): PrintScriptIterator<ASTNode> {
        val tokenBuffer = TokenBuffer(lexer)
        return when (version) {
            "1.0" -> {
                val provider = ParserProvider10()
                provider.provide(tokenBuffer)
            }
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }

    // ========================================
    // PIPELINE COMPLETO
    // ========================================

    /**
     * Ejecuta el pipeline: Source → Tokens → AST Statements
     */
    fun parse(sourceCode: String, version: String = "1.0"): List<ASTNode> {
        val lexer = createLexer(sourceCode, version)
        val parser = createParser(lexer, version)
        val statements = mutableListOf<ASTNode>()

        while (parser.hasNext()) {
            statements.add(parser.getNext())
        }

        return statements
    }

    // ========================================
    // HELPERS PARA TESTING
    // ========================================

    /**
     * Verifica que la cantidad de statements sea la esperada
     */
    fun assertStatementCount(sourceCode: String, expectedCount: Int, version: String = "1.0") {
        val statements = parse(sourceCode, version)

        if (statements.size != expectedCount) {
            throw AssertionError(
                "Statement count mismatch:\n" +
                    "Expected: $expectedCount\n" +
                    "Actual:   ${statements.size}\n" +
                    "Statements: ${statements.map { it::class.simpleName }}"
            )
        }
    }

    // ========================================
    // CASOS DE TEST COMUNES
    // ========================================

    /**
     * Casos de test típicos basados en el TCK
     */
    object TestCases {

        val SIMPLE_DECLARE_ASSIGN = """
            let result: Number;
            result = 5;
            println(result);
        """.trimIndent()

        val ARITHMETIC_OPERATIONS = """
            let numberResult: Number = 5 * 5 - 8;
            println(numberResult);
        """.trimIndent()

        val STRING_AND_NUMBER_CONCAT = """
            let someNumber: Number = 1;
            let someString: String = "hello world ";
            println(someString + someNumber);
        """.trimIndent()

        val ARITHMETIC_DECIMAL = """
            let pi: Number;
            pi = 3.14;
            println(pi / 2);
        """.trimIndent()

        // Version 1.1 cases (para cuando lo implementes)
        val IF_STATEMENT_TRUE = """
            const booleanValue: boolean = true;
            if(booleanValue) {
                println("if statement working correctly");
            }
            println("outside of conditional");
        """.trimIndent()

        val ELSE_STATEMENT_FALSE = """
            const booleanResult: boolean = false;
            if(booleanResult) {
                println("else statement not working correctly");
            } else {
                println("else statement working correctly");
            }
            println("outside of conditional");
        """.trimIndent()
    }
}
