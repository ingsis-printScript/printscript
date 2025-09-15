package org.example.linter.rules.functionargument

import org.example.ast.ASTNode
import org.example.ast.statements.functions.PrintFunction
import org.example.linter.LinterConfiguration
import org.example.linter.data.LinterViolation
import org.example.linter.rules.Rule
import org.example.linter.rules.functionargument.checker.FunctionArgumentChecker
import kotlin.reflect.KClass

class PrintArgumentRule(
    private val prohibitedNodes: Set<KClass<out ASTNode>>,
    private val supportedNodes: Set<KClass<out ASTNode>>

) : Rule {

    private val violations = mutableListOf<LinterViolation>()
    private val checker = FunctionArgumentChecker(prohibitedNodes, supportedNodes)

    override fun check(node: ASTNode, configuration: LinterConfiguration): List<LinterViolation> {
        if (!isEnabled(configuration)) return emptyList()

        violations.clear()
        violations.addAll(
            checker.checkNodes(
                node = node,
                shouldCheckNode = { it is PrintFunction },
                extractValue = { (it as PrintFunction).value },
                getFunctionName = { "println()" },
                getRange = { (it as PrintFunction).range }
            )
        )

        return violations.toList()
    }

    override fun isEnabled(configuration: LinterConfiguration): Boolean {
        return configuration.getBoolean("println_only_literals_and_identifiers")
    }
}