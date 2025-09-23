package org.example.formatter.rules

import org.example.common.configuration.RulesConfiguration
import org.example.formatter.FormatterContext
import org.example.token.Token

class PreserveOriginalSpaceRule : Rule {
    override fun isEnabled(configuration: RulesConfiguration) = true
    override fun before(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        ctx.emitOriginalSpace(prev, cur)
    }
}
