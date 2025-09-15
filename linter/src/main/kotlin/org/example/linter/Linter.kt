package org.example.linter

import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator
import org.example.linter.configurationreaders.ConfigurationReader
import org.example.linter.data.LinterReport
import org.example.linter.data.LinterViolation
import org.example.linter.rules.Rule

class Linter(
    private val iterator: PrintScriptIterator<ASTNode>,
    private val rules: List<(Rule)>,
    private val configurationReader: ConfigurationReader
): PrintScriptIterator<ASTNode> {

    fun analyze(ast: ASTNode, configPath: String): LinterReport {
        val configData = configurationReader.read(configPath)
        val configuration = LinterConfiguration(configData)

        val violations = mutableListOf<LinterViolation>()
        for (rule in rules) {
            violations.addAll(rule.check(ast, configuration))
        }

        return LinterReport(violations)
    }

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun getNext(): ASTNode {
        val node = iterator.getNext()
    }
}
