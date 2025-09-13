package org.example.linter.rules

import org.example.ast.ASTNode
import org.example.ast.expressions.Expression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.functions.PrintFunction
import org.example.linter.LinterConfiguration
import org.example.linter.data.LinterViolation
import kotlin.reflect.KClass

class PrintArgumentRule(
    private val prohibitedNodes: Set<KClass<out ASTNode>>
) : Rule {

    private val violations = mutableListOf<LinterViolation>()
    private lateinit var currentConfig: LinterConfiguration

    override fun check(node: ASTNode, configuration: LinterConfiguration): List<LinterViolation> {
        if (!isEnabled(configuration)) return emptyList()

        violations.clear()
        currentConfig = configuration
        handlers[node::class]?.invoke(node, this) //lo mismo, switch case mas lindo (posta) que tenga un else (type safety). aca es solo printFunction
        return violations.toList()
    }

    override fun isEnabled(configuration: LinterConfiguration): Boolean {
        return configuration.getBoolean("println_only_literals_and_identifiers")
    }

    private fun checkPrintlnArguments(printFunction: PrintFunction) {
        val value: OptionalExpression = printFunction.value
        when (value) {
            is OptionalExpression.NoExpression -> { return }
            is OptionalExpression.HasExpression -> {
                val expression: Expression = value.expression
                if (expression::class in prohibitedNodes) {
                    violations
                        .add(
                            LinterViolation(
                                "println() can not contain $expression",
                                printFunction.range
                            )
                        )
                }
            }
        }
    }
}
