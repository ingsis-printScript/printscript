package org.example.linter.rules.functionargument

import org.example.ast.ASTNode
import org.example.ast.expressions.ReadInputExpression
import org.example.common.ErrorHandler
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.linter.LinterConfiguration
import org.example.linter.rules.Rule
import org.example.linter.rules.functionargument.checker.FunctionArgumentChecker
import kotlin.reflect.KClass

class ReadInputArgumentRule(
    private val prohibitedNodes: Set<KClass<out ASTNode>>,
    private val supportedNodes: Set<KClass<out ASTNode>>,
    private val nodeHandler: (ASTNode, (ReadInputExpression) -> Result) -> Result
) : Rule {

    private val checker = FunctionArgumentChecker(prohibitedNodes, supportedNodes)

    override fun check(node: ASTNode, configuration: LinterConfiguration, errorHandler: ErrorHandler): Result {
        if (!isEnabled(configuration)) return Success(Unit)

        return nodeHandler(node) { ri ->
            checker.checkNodes(
                node = ri,
                shouldCheckNode = { it is ReadInputExpression },
                extractValue = { (it as ReadInputExpression).value },
                getFunctionName = { "readInput()" },
                getRange = { (it as ReadInputExpression).range },
                errorHandler
            )
        }
    }

    override fun isEnabled(configuration: LinterConfiguration): Boolean {
        return configuration.getBoolean("mandatory-variable-or-literal-in-readInput")
    }
}
