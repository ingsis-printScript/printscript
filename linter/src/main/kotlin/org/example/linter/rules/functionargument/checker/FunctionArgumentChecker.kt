package org.example.linter.rules.functionargument.checker

import org.example.ast.ASTNode
import org.example.ast.expressions.Expression
import org.example.ast.expressions.OptionalExpression
import org.example.common.ErrorHandler
import org.example.common.results.Error
import org.example.common.results.Result
import org.example.common.results.Success
import kotlin.reflect.KClass

class FunctionArgumentChecker(
    private val prohibitedNodes: Set<KClass<out ASTNode>>,
    private val supportedNodes: Set<KClass<out ASTNode>>
) {

    fun checkNodes(
        node: ASTNode,
        shouldCheckNode: (ASTNode) -> Boolean,
        extractValue: (ASTNode) -> OptionalExpression,
        getFunctionName: () -> String,
        getRange: (ASTNode) -> Any,
        errorHandler: ErrorHandler
    ): Result {

        if (node::class !in supportedNodes) {
            return Error("Node ${node::class.simpleName} is not supported.")
        }

        if (!shouldCheckNode(node)) return Success(Unit)

        val value: OptionalExpression = extractValue(node)
        when (value) {
            is OptionalExpression.NoExpression -> {
                return Success(Unit)
            }
            is OptionalExpression.HasExpression -> {
                val expression: Expression = value.expression
                if (expression::class !in supportedNodes) {
                    return Error("Node ${expression::class.simpleName} is not supported.")
                }
                if (expression::class in prohibitedNodes) {
                    val fn  = getFunctionName()
                    val rng = getRange(node)
                    errorHandler.handleError("Argument $fn cannot have $expression at range $rng")
                }
            }
        }
        return Success(Unit)
    }
}