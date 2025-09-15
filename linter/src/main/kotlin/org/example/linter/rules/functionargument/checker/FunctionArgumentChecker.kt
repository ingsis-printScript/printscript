package org.example.linter.rules.functionargument.checker

import org.example.ast.ASTNode
import org.example.ast.expressions.Expression
import org.example.ast.expressions.OptionalExpression
import org.example.common.Range
import org.example.linter.data.LinterViolation
import kotlin.reflect.KClass

class FunctionArgumentChecker(
    private val prohibitedNodes: Set<KClass<out ASTNode>>,
    private val supportedNodes: Set<KClass<out ASTNode>>
) {

    fun checkNodes(node: ASTNode, shouldCheckNode: (ASTNode) -> Boolean, extractValue: (ASTNode) -> OptionalExpression, getFunctionName: () -> String, getRange: (ASTNode) -> Any
    ): List<LinterViolation> {
        val violations = mutableListOf<LinterViolation>()

        if (node::class in supportedNodes && shouldCheckNode(node)) {
            val value: OptionalExpression = extractValue(node)
            when (value) {
                is OptionalExpression.NoExpression -> { return emptyList() }
                is OptionalExpression.HasExpression -> {
                    val expression: Expression = value.expression
                    if (expression::class in prohibitedNodes) {
                        violations.add(
                            LinterViolation(
                                "${getFunctionName()} can not contain $expression",
                                getRange(node) as Range
                            )
                        )
                    }
                }
            }
        }



        return violations
    }
}