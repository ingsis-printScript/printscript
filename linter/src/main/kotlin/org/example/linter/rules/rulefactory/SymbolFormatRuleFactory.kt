package org.example.linter.rules.rulefactory

import org.example.linter.LinterConfiguration
import org.example.linter.rules.Rule
import org.example.linter.rules.SymbolFormatRule

class SymbolFormatRuleFactory : RuleFactory {
    override fun create(config: LinterConfiguration): Rule {
        return SymbolFormatRule(config)
    }
}
