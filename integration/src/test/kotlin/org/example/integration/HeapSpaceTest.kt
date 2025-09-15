package org.example.integration

import org.example.ast.ASTNode
import org.example.common.results.Error
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.lexer.provider.LexerVersionProvider
import org.example.parser.Parser
import org.example.parser.TokenBuffer
import org.example.parser.provider.ParserVersionProvider
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HeapSpaceTest {
    @Test
    fun `parser debe avanzar el buffer cuando ningun patron matchea`() {
        // given: un input con 'if' y sin ConditionParser en v1_0
        val src = listOf("""if(a) println("x"); """).iterator()
        val lexer = LexerVersionProvider().with("1.0").provide(src)
        val buf = TokenBuffer(lexer)
        var output: Result = Error("no success received")

        // Parsers de v1.0 (sin ConditionParser)
        val parser = ParserVersionProvider().with("1.0").provide(buf)

        // guardamos posición inicial
        val startPos = buf.index() // si no existe, ver nota abajo

        // intento de parseo 1 vez (tu función actual; si no la tenés, invocá tu Parser.getNext())
        // Recomendado: factorizar en parseNextStatementOrSync(buf, parsers, handler)
        // Forzamos una sola iteración
        try { output = parser.getNext() } catch (ex: Exception) { /* ignore para la prueba */ }

        // posición luego del intento
        val endPos = buf.index()

        println(output)

        // then:
        // 1) Debe haberse reportado algún error de "statement inválido o no soportado"
        assertTrue(output is Error, "No se reportó error ante un statement inválido")

        // 2) Debe haber avanzado el buffer para evitar loop (pos > start)
        assertTrue(endPos > startPos, "El TokenBuffer no avanzó: posible bucle infinito")
    }

}