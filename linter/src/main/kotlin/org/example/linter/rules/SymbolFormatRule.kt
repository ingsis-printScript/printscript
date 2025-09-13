package org.example.linter.rules

import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression
import org.example.common.Range
import org.example.common.enums.SymbolFormat
import org.example.linter.LinterConfiguration
import org.example.linter.data.LinterViolation
import org.example.linter.rules.symbolformat.SymbolFormatChecker

class SymbolFormatRule(
    private val formatCheckers: Map<SymbolFormat, SymbolFormatChecker>
) : Rule {

    private val violations = mutableListOf<LinterViolation>()
    private lateinit var currentConfig: LinterConfiguration

    override fun check(node: ASTNode, configuration: LinterConfiguration): List<LinterViolation> {
        if (!isEnabled(configuration)) return emptyList()

        violations.clear()
        currentConfig = configuration
        handlers[node::class]?.invoke(node, this) //hacer switch case mas lindo (posta) que tenga un else (type safety)
        return violations.toList()
    }

    override fun isEnabled(configuration: LinterConfiguration): Boolean {
        return configuration.getString("identifier_format") != null
    }

    private fun checkSymbolFormat(symbol: SymbolExpression) {
        val formatString = currentConfig.getString("identifier_format") ?: return
        val expectedFormat = SymbolFormat.fromString(formatString)
        val checker = formatCheckers[expectedFormat] ?: return
        if (!checker.isValid(symbol.value)) {
            val range = Range(symbol.position, symbol.position)
            violations.add(
                LinterViolation(
                    checker.message(symbol.value, range),
                    range
                )
            )
        }
    }
}
