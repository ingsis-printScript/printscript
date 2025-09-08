package org.example.linter.rules.rulefactory

import org.example.linter.LinterConfiguration
import org.example.linter.rules.PrintArgumentRule
import org.example.linter.rules.Rule

class PrintArgumentRuleFactory : RuleFactory {
    override fun create(config: LinterConfiguration): Rule {
        return PrintArgumentRule(config)
    }
}