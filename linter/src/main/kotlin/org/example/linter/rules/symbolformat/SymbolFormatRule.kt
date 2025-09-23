package org.example.linter.rules.symbolformat

import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression
import org.example.common.ErrorHandler
import org.example.common.Range
import org.example.common.configuration.RulesConfiguration
import org.example.common.enums.SymbolFormat
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.linter.rules.Rule
import org.example.linter.rules.symbolformat.checker.SymbolFormatChecker
import kotlin.collections.get
import kotlin.reflect.KClass

class SymbolFormatRule(
    private val formatCheckers: Map<SymbolFormat, SymbolFormatChecker>,
    private val supportedNodes: Set<KClass<out ASTNode>>,
    private val nodeHandler: (ASTNode, (SymbolExpression) -> Unit) -> Unit
) : Rule {

    private lateinit var currentConfig: RulesConfiguration
    private lateinit var errorHandler: ErrorHandler

    override fun check(node: ASTNode, configuration: RulesConfiguration, errorHandler: ErrorHandler): Result {
        if (!isEnabled(configuration)) return Success(Unit)

        this.currentConfig = configuration
        this.errorHandler = errorHandler
        checkNodes(node)
        return Success(Unit)
    }

    override fun isEnabled(configuration: RulesConfiguration): Boolean {
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
            errorHandler.handleError(checker.message(symbol.value, range))
        }
    }
}
