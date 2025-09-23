package org.example.formatter.rules

import org.example.common.configuration.RulesConfiguration
import org.example.formatter.FormatterContext
import org.example.token.Token

class SpaceAroundColonRule : Rule, ClaimsToken {
    override fun isEnabled(configuration: RulesConfiguration) =
        configuration.getBoolean("enforce-spacing-before-colon-in-declaration") ||
                configuration.getBoolean("enforce-spacing-after-colon-in-declaration")

    override fun before(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        val wantBefore = ctx.configuration.getBoolean("enforce-spacing-before-colon-in-declaration")
        val wantAfter  = ctx.configuration.getBoolean("enforce-spacing-after-colon-in-declaration")

        if (isColon(cur)) {
            if (wantBefore) ctx.setPendingSpaces(1)
        }
        if (isColon(prev)) {
            if (wantAfter) ctx.setPendingSpaces(1)
        }
    }

    override fun claims(prev: Token?, cur: Token?, next: Token?, cfg: RulesConfiguration): Boolean {
        if (!isEnabled(cfg)) return false
        return isColon(cur) || isColon(prev)
    }

    private fun isColon(t: Token?) = t?.type == org.example.token.TokenType.PUNCTUATION && t.value == ":"


}
