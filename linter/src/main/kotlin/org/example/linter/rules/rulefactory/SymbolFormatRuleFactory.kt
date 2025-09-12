package org.example.linter.rules.rulefactory

import org.example.common.enums.SymbolFormat
import org.example.linter.LinterConfiguration
import org.example.linter.rules.Rule
import org.example.linter.rules.SymbolFormatRule
import org.example.linter.rules.symbolformat.SymbolFormatChecker

class SymbolFormatRuleFactory(
    private val formatCheckers: Map<SymbolFormat, SymbolFormatChecker>
) : RuleFactory {
    override fun create(config: LinterConfiguration): Rule {
        return SymbolFormatRule(config, formatCheckers)
    }
}
