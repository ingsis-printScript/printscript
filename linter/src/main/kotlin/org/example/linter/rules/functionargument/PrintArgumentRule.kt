package org.example.linter.rules.functionargument

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