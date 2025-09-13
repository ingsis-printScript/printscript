package org.example.linter.rules

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.Expression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.VariableImmutableDeclarator
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
        checkNodes(node)
        return violations.toList()
    }

    override fun isEnabled(configuration: LinterConfiguration): Boolean {
        return configuration.getBoolean("println_only_literals_and_identifiers")
    }

    fun checkNodes(node: ASTNode) {
        when (node) {
            is PrintFunction -> {checkPrintlnArguments(node)}
            is BooleanExpression -> {}
            is NumberExpression -> {}
            is StringExpression -> {}
            is SymbolExpression -> {}
            is BinaryExpression -> {}
            is VariableAssigner -> {}
            is VariableDeclarator -> {}
            is VariableImmutableDeclarator -> {}
            else -> throw IllegalArgumentException("Unsupported node type: $node")
        }
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
