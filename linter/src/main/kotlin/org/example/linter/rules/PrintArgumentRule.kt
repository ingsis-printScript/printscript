package org.example.linter.rules

import org.example.ast.ASTNode
import org.example.ast.expressions.Expression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.functions.PrintFunction
import org.example.linter.LinterConfiguration
import org.example.linter.data.LinterViolation
import kotlin.reflect.KClass

class PrintArgumentRule(private val config: LinterConfiguration, private val handlers: Map<KClass<out ASTNode>, (ASTNode) -> Unit>,
    private val prohibitedNodes: Set<ASTNode>) : Rule {

    private val violations = mutableListOf<LinterViolation>()

    override fun check(node: ASTNode): List<LinterViolation> {
        if (!isEnabled()) return emptyList()

        violations.clear()
        handlers[node::class]?.invoke(node)
        return violations.toList()
    }

    override fun isEnabled(): Boolean {
        return config.getBoolean("println_only_literals_and_identifiers")
    }

    private fun checkPrintlnArguments(printFunction: PrintFunction) {
        val value: OptionalExpression = printFunction.value
        when (value) {
            is OptionalExpression.NoExpression -> { return }
            is OptionalExpression.HasExpression -> {
                val expression: Expression = value.expression
                if (expression in prohibitedNodes) violations
                    .add(LinterViolation(
                        "println() can not contain $expression",
                    printFunction.range))
            }
        }
    }
}
