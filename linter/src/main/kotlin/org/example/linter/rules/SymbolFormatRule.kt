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
import org.example.linter.rules.symbolformat.SymbolFormatChecker
import kotlin.reflect.KClass

class SymbolFormatRule(
    private val config: LinterConfiguration,
    private val handlers: Map<KClass<out ASTNode>, (ASTNode) -> Unit>,
    private val formatCheckers: Map<SymbolFormat, SymbolFormatChecker>
) : Rule {

    private val violations = mutableListOf<LinterViolation>()

    override fun check(node: ASTNode): List<LinterViolation> {
        if (!isEnabled()) return emptyList()

        violations.clear()
        handlers[node::class]?.invoke(node)
        return violations.toList()
    }

    override fun isEnabled(): Boolean {
        return config.getString("identifier_format") != null
    }

    private fun checkSymbolFormat(symbol: SymbolExpression) {
        val formatString = config.getString("identifier_format") ?: return
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
