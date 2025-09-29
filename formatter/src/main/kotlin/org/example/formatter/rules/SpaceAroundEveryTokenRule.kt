package org.example.formatter.rules

import org.example.common.configuration.RulesConfiguration
import org.example.formatter.FormatterContext
import org.example.token.Token

class SpaceAroundEveryTokenRule(
    private val otherRules: List<Rule>
) : Rule {

    override fun isEnabled(configuration: RulesConfiguration): Boolean =
        configuration.getBoolean("mandatory-single-space-separation")

    override fun before(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        if (prev == null) return
        if (cur.position.line != prev.position.line) return

        val cfg = ctx.configuration

        // Si alguna otra regla "reclama" este borde, no intervenimos.
        val claimed = otherRules.asSequence()
            .filterIsInstance<ClaimsToken>()
            .any { it.claims(prev, cur, next, cfg) }
        if (claimed) return

        // Reglas locales mínimas para no romper puntuación común:
        val curIsTightPunct = (cur.value == "," || cur.value == ";")
        val prevIsOpenOrComma = (prev.value == ",")
        if (curIsTightPunct || prevIsOpenOrComma) return

        ctx.setPendingSpaces(1)
    }
}
