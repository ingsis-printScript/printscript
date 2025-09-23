package org.example.formatter.rules

import org.example.common.configuration.RulesConfiguration
import org.example.formatter.FormatterContext
import org.example.token.Token
import org.example.token.TokenType

class LineAfterSemicolonRule : Rule {
    override fun isEnabled(configuration: RulesConfiguration) =
        true

    override fun after(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        if (isSemicolon(cur) && next != null) {
            ctx.setPendingNewlines(1)
        }
    }

    private fun isSemicolon(t: Token?) = t?.type == TokenType.PUNCTUATION && t.value == ";"

}
