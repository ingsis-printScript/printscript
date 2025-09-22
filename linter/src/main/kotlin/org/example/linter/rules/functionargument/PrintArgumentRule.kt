package org.example.linter.rules.functionargument

import org.example.ast.ASTNode
import org.example.ast.statements.functions.PrintFunction
import org.example.common.ErrorHandler
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.common.configuration.configurationreaders.RulesConfiguration
import org.example.linter.rules.Rule
import org.example.linter.rules.functionargument.checker.FunctionArgumentChecker
import kotlin.reflect.KClass

class PrintArgumentRule(
    private val prohibitedNodes: Set<KClass<out ASTNode>>,
    private val supportedNodes: Set<KClass<out ASTNode>>

) : Rule {

    private val checker = FunctionArgumentChecker(prohibitedNodes, supportedNodes)

    override fun check(node: ASTNode, configuration: RulesConfiguration, errorHandler: ErrorHandler): Result {
        if (!isEnabled(configuration)) return Success(Unit)

        val result = checker.checkNodes(
            node = node,
            shouldCheckNode = { it is PrintFunction },
            extractValue = { (it as PrintFunction).value },
            getFunctionName = { "println()" },
            getRange = { (it as PrintFunction).range },
            errorHandler
        )
        return result
    }

    override fun isEnabled(configuration: RulesConfiguration): Boolean {
        return configuration.getBoolean("mandatory-variable-or-literal-in-println")
    }
}
