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
        if (inPrintlnStmt && isSemicolon(cur)) {
            if (next != null) {
                val n = count(ctx.configuration)
                ctx.setPendingNewlines(1 + n)
            }
            inPrintlnStmt = false
        }
    }

    private fun isSemicolon(t: Token?) = t?.type == TokenType.PUNCTUATION && t.value == ";"
    private fun isPrintln(t: Token?) = t != null && t.type == TokenType.SYMBOL && t.value == "println"
}
