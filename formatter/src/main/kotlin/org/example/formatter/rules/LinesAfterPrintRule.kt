package org.example.formatter.rules

import org.example.common.configuration.RulesConfiguration
import org.example.formatter.FormatterContext
import org.example.token.Token
import org.example.token.TokenType

class LinesAfterPrintRule : Rule {

    private fun count(c: RulesConfiguration): Int {
        val after = c.getInt("line-breaks-after-println", 0)
        return after.coerceIn(0, 3) // si querés permitir hasta 3
    }

    private var inPrintlnStmt: Boolean = false

    override fun isEnabled(configuration: RulesConfiguration): Boolean = count(configuration) > 0

    override fun before(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        if (isPrintln(cur)) {
            inPrintlnStmt = true
        }
    }

    override fun after(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        // Solo al cerrar con ';' aplicamos los blank lines
        if (inPrintlnStmt && isSemicolon(cur)) {
            // No agregar nada si es el último token (evitar \n final de archivo)
            if (next != null) {
                val n = count(ctx.configuration)
                // “Una línea más” por el salto propio del statement + (n) líneas en blanco
                ctx.newlineOnce()     // termina la línea del println(...)
                repeat(n) { ctx.newlineOnce() } // n líneas vacías
            }
            inPrintlnStmt = false
        }
    }

    private fun isSemicolon(t: Token?) = t?.type == TokenType.PUNCTUATION && t.value == ";"
    private fun isPrintln(t: Token?)   = t != null && t.type == TokenType.SYMBOL && t.value == "println"

}
