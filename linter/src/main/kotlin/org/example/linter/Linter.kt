package org.example.linter

import org.example.ast.ASTNode
import org.example.linter.configurationreaders.ConfigurationReader
import org.example.linter.data.LinterReport
import org.example.linter.data.LinterViolation
import org.example.linter.rules.Rule
import org.example.linter.rules.rulefactory.RuleFactory

class Linter(
    private val ruleFactories: List<(RuleFactory)>,
    private val configurationReader: ConfigurationReader
) {

    fun analyze(ast: ASTNode, configPath: String): LinterReport {
        val configData = configurationReader.read(configPath)
        val configuration = LinterConfiguration(configData)

        val rules = mutableListOf<Rule>()
        for (factory in ruleFactories) {
            val rule = factory.create(configuration)
            rules.add(rule)
        }

        val enabledRules = rules.filter { it.isEnabled() }
        val violations = mutableListOf<LinterViolation>()
        for (rule in enabledRules) {
            violations.addAll(rule.check(ast))
        }

        return LinterReport(
            violations = violations
        )
    }
}
