package org.example.linter.rules

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.ast.statements.functions.PrintFunction
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
        checkNodes(node)
        return violations.toList()
    }

    override fun isEnabled(configuration: LinterConfiguration): Boolean {
        return configuration.getString("identifier_format") != null
    }

    private fun checkNodes(node: ASTNode) {
        when (node) {
            is SymbolExpression -> checkSymbolFormat(node)
            is BinaryExpression -> {
                checkNodes(node.left)
                checkNodes(node.right)
            }
            is PrintFunction -> {
                checkOptionalExpression(node.value)
            }
            is VariableAssigner -> {
                checkNodes(node.symbol)
                checkOptionalExpression(node.value)
            }
            is VariableDeclarator -> {
                checkNodes(node.symbol)
                checkOptionalExpression(node.value)
            }
            is VariableImmutableDeclarator -> {
                checkNodes(node.symbol)
                checkOptionalExpression(node.value)
            }
            is BooleanExpression -> {}
            is NumberExpression -> {}
            is StringExpression -> {}
            else -> throw IllegalArgumentException("Unsupported node type: $node")
        }
    }

    private fun checkOptionalExpression(value: OptionalExpression) {
        if (value is OptionalExpression.HasExpression) {
            checkNodes(value.expression)
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
