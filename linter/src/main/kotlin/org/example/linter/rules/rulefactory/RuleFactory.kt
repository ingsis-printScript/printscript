package org.example.linter.rules.rulefactory

import org.example.linter.LinterConfiguration
import org.example.linter.rules.Rule

interface RuleFactory {
    fun create(config: LinterConfiguration): Rule
}
