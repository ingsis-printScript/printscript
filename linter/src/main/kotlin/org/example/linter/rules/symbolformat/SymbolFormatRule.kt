package org.example.linter.rules.symbolformat

import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression
import org.example.common.Range
import org.example.common.enums.SymbolFormat
import org.example.linter.LinterConfiguration
import org.example.linter.data.LinterViolation
import org.example.linter.rules.Rule
import org.example.linter.rules.symbolformat.checker.SymbolFormatChecker
import kotlin.collections.get
import kotlin.reflect.KClass

class SymbolFormatRule(
    private val formatCheckers: Map<SymbolFormat, SymbolFormatChecker>,
    private val supportedNodes: Set<KClass<out ASTNode>>,
    private val nodeHandler: (ASTNode, (SymbolExpression) -> Unit) -> Unit
) : Rule {

    private val violations = mutableListOf<LinterViolation>()
    private lateinit var currentConfig: LinterConfiguration

    override fun check(node: ASTNode, configuration: LinterConfiguration): List<LinterViolation> {
        if (!isEnabled(configuration)) return emptyList()

        violations.clear()
        currentConfig = configuration
        checkNodes(node)
        return violations.toList()
    }

    override fun isEnabled(configuration: LinterConfiguration): Boolean {
        return configuration.getString("identifier_format") != null
    }

    private fun checkNodes(node: ASTNode) {
        if (node::class in supportedNodes) {
            nodeHandler(node) { symbol -> checkSymbolFormat(symbol) }
        }
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