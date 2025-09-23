package org.example.formatter.rules

import org.example.common.configuration.RulesConfiguration
import org.example.formatter.FormatterContext
import org.example.token.Token
import org.example.token.TokenType

class NoSpaceAroundEqualsRule : Rule, ClaimsToken {

    override fun isEnabled(configuration: RulesConfiguration): Boolean =
        configuration.getBoolean("enforce-no-spacing-around-equals")

    override fun before(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        // Si el token actual es '=', bloqueamos espacio antes de '=' (gap previo)
        if (isEq(cur)) {
            ctx.requestNoNextSpace()
        }
        // Si el token anterior fue '=', bloqueamos espacio antes del token actual (o sea, después de '=')
        if (isEq(prev)) {
            ctx.requestNoNextSpace()
        }
    }

    override fun claims(prev: Token?, cur: Token?, next: Token?, cfg: RulesConfiguration): Boolean {
        if (!isEnabled(cfg)) return false
        return isEq(cur) || isEq(prev)
    }

    private fun isEq(t: Token?) = t?.type == TokenType.PUNCTUATION && t.value == "="
}
