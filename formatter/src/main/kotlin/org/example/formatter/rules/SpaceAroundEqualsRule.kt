package org.example.formatter.rules

import org.example.common.configuration.RulesConfiguration
import org.example.formatter.FormatterContext
import org.example.token.Token
import org.example.token.TokenType

class SpaceAroundEqualsRule : Rule, ClaimsToken {
    private fun enabled(c: RulesConfiguration) =
        c.getBoolean("enforce-spacing-around-equals") || c.getBoolean("enforce-spacing-before-equals") || c.getBoolean("enforce-spacing-after-equals")

    override fun isEnabled(configuration: RulesConfiguration) = enabled(configuration)

    override fun before(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        val around = ctx.configuration.getBoolean("enforce-spacing-around-equals")
        val wantBefore = around || ctx.configuration.getBoolean("enforce-spacing-before-equals")
        val wantAfter = around || ctx.configuration.getBoolean("enforce-spacing-after-equals")

        // Lado "antes": cuando el token actual ES '='
        if (isEqual(cur)) {
            if (wantBefore) ctx.setPendingSpaces(1) else ctx.clearPendingSpaces()
        }

        // Lado "después": cuando el token anterior ES '='
        if (isEqual(prev)) {
            if (wantAfter) ctx.setPendingSpaces(1) else ctx.clearPendingSpaces()
        }
    }

    override fun claims(prev: Token?, cur: Token?, next: Token?, cfg: RulesConfiguration): Boolean {
        if (!isEnabled(cfg)) return false
        return isEqual(cur) || isEqual(prev)
    }

    private fun isEqual(t: Token?) = t?.type == TokenType.PUNCTUATION && t.value == "="
}
