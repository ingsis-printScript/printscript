package org.example.formatter.rules

import org.example.common.configuration.RulesConfiguration
import org.example.common.enums.Operator
import org.example.formatter.FormatterContext
import org.example.token.Token
import org.example.token.TokenType

class SpaceAroundOperatorRule : Rule, ClaimsToken {
    override fun isEnabled(configuration: RulesConfiguration) =
        configuration.getBoolean("mandatory-space-surrounding-operations")

    override fun before(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        if (isOperator(prev) || isOperator(cur)) ctx.setPendingSpaces(1)
    }

    override fun claims(prev: Token?, cur: Token?, next: Token?, cfg: RulesConfiguration): Boolean {
        if (!isEnabled(cfg)) return false
        return isOperator(prev) || isOperator(cur)
    }

    private fun isOperator(t: Token?) = t?.type == TokenType.OPERATOR && Operator.fromString(t.value) != null
}
