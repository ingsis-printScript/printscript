package org.example.linter.rules

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.Range
import org.example.common.enums.SymbolFormat
import org.example.linter.LinterConfiguration
import org.example.linter.data.LinterViolation

class SymbolFormatRule(private val config: LinterConfiguration) : Rule {

    private val violations = mutableListOf<LinterViolation>()

    override fun check(node: ASTNode): List<LinterViolation> {
        if (!isEnabled()) return emptyList()

        violations.clear()
        node.accept(this)
        return violations.toList()
    }

    override fun isEnabled(): Boolean {
        return config.getString("identifier_format") != null
    }

    override fun visit(node: ASTNode): List<LinterViolation> {
        // recibiria un mapa tambien. Cosa de intentar hacerlo mas extensible.
        when (node) {
            is VariableDeclarator -> {
                visit(node.symbol)
                if (node.value is OptionalExpression.HasExpression) visit((node.value as OptionalExpression.HasExpression).expression)
            }
            is VariableAssigner -> {
                visit(node.symbol)
                if (node.value is OptionalExpression.HasExpression) visit((node.value as OptionalExpression.HasExpression).expression)
            }
            is BinaryExpression -> {
                visit(node.left)
                visit(node.right)
            }
            is PrintFunction -> if (node.value is OptionalExpression.HasExpression) visit((node.value as OptionalExpression.HasExpression).expression)
            is SymbolExpression -> checkSymbolFormat(node)
        }
        return violations
    }

    private fun checkSymbolFormat(symbol: SymbolExpression) {
        val formatString = config.getString("identifier_format") ?: return
        val expectedFormat = parseSymbolFormat(formatString) ?: return

        when (expectedFormat) {
            SymbolFormat.CAMEL_CASE -> {
                if (!isCamelCase(symbol.value)) {
                    violations.add(
                        LinterViolation(
                            "Identifier '$symbol.value' should be in camelCase format",
                            Range(
                                symbol.position,
                                symbol.position
                            )
                        )
                    )
                }
            }
            SymbolFormat.SNAKE_CASE -> {
                if (!isSnakeCase(symbol.value)) {
                    violations.add(
                        LinterViolation(
                            message = "Identifier '$symbol.value' should be in snake_case format",
                            Range(
                                symbol.position,
                                symbol.position
                            )
                        )
                    )
                }
            }
        }
    }

    private fun parseSymbolFormat(value: String): SymbolFormat? {
        return when (value.lowercase()) {
            "camel_case", "camelcase" -> SymbolFormat.CAMEL_CASE
            "snake_case", "snakecase" -> SymbolFormat.SNAKE_CASE
            else -> null
        }
    }

    private fun isCamelCase(identifier: String): Boolean {
        if (identifier.isEmpty()) return false

        if (!identifier[0].isLowerCase()) return false

        if (identifier.contains('_')) return false

        return identifier.all { it.isLetterOrDigit() }
    }

    private fun isSnakeCase(identifier: String): Boolean {
        if (identifier.isEmpty()) return false

        if (identifier != identifier.lowercase()) return false

        if (!identifier.all { it.isLetterOrDigit() || it == '_' }) return false

        if (identifier.endsWith('_')) return false

        if (identifier.contains("__")) return false

        return true
    }
}
