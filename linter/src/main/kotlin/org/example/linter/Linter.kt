package org.example.linter

import org.example.ast.ASTNode
import org.example.linter.rules.Rule

class Linter(
    private val rules: List<Rule>,
    private val configurationReader: ConfigurationReader,
    private val configurationFactory: ConfigurationFactory
    ) {
        fun analyze(ast: ASTNode, configPath: String): LinterReport {
            val configData = configurationReader.read(configPath)
            val configuration = configurationFactory.createConfiguration(configData)

            val violations = mutableListOf<LinterViolation>()
            val enabledRules = rules.filter { it.isEnabled(configuration) }

            for (rule in enabledRules) {
                violations.addAll(rule.check(ast, configuration))
            }

            return LinterReport(violations, enabledRules.map { it.getName() })
        }
    }

data class LinterReport(
    val violations: List<LinterViolation>,
    val appliedRules: List<String>
) {
    fun hasViolations(): Boolean = violations.isNotEmpty()

    fun getViolationCount(): Int = violations.size

    fun getViolationsByType(): Map<Severity, List<LinterViolation>> {
        return violations.groupBy { it.severity }
    }
}