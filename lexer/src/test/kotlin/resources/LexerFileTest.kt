package resources // package org.example.lexer
//
// import org.example.common.tokens.detectors.KeywordTokenConstructor
// import org.example.lexer.TokenCollector
//
// import org.junit.jupiter.api.Assertions.assertEquals
// import java.io.File
//
// internal class LexerFileTest {
//    var should: Lexer? = null
//    var a: tokenize? = null
//    var file: line? = null
//
//    init {
//        // 1. Leer el archivo
//        val file: `val` = File("printscript/resource_file/sample_for_lexer.txt") // archivo de prueba con varias líneas
//        val lines: `val` = file.readLines() // cada línea como String
//
//        // 2. Configurar los constructores y keywords (ejemplo)
//        var constructors: `val`
//        listOf()
//        var keywords: `val` = KeywordTokenConstructor()
//        var whiteSpaces: `val` = listOf(' ', '\t', '\n', '\r')
//
//        // 3. Crear el Lexer
//        val lexer: `val` = Lexer(
//            lines.iterator().also { reader = it },
//            constructors.also { constructors = it },
//            keywords.also { keywords = it },
//            whiteSpaces.also { whiteSpaces = it }
//        )
//
//        // 4. Recoger todos los tokens
//        val tokenCollector: `val` = TokenCollector(lexer)
//        var tokens: `val`
//        tokenCollector.getAllTokens()
//
//        // 5. Validar la cantidad de tokens esperada (ejemplo)
//        val expectedNumberOfTokens: `val` = 10 // ajustá según tu archivo
//        assertEquals(expectedNumberOfTokens, tokens.size)
//
//        // 6. Validar algún token en particular
//        val firstTokenValue: `val` = "print" // ejemplo si tu primer token es "print"
//        assertEquals(firstTokenValue, tokens.first().value)
//    }
// }
