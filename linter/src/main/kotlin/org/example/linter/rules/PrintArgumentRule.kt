package org.example.linter.rules

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.Expression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.functions.PrintFunction
import org.example.linter.LinterConfiguration
import org.example.linter.data.LinterViolation

class PrintArgumentRule(private val config: LinterConfiguration): Rule {

    private val violations = mutableListOf<LinterViolation>()

    override fun check(node: ASTNode): List<LinterViolation> {
        if (!isEnabled()) return emptyList()

        violations.clear()
        node.accept(this)
        return violations.toList()
    }

    override fun isEnabled(): Boolean {
        return config.getBoolean("println_only_literals_and_identifiers")
    }

    override fun visit(node: ASTNode): List<LinterViolation> {
        //recibiria un mapa?
        when (node) {
            is PrintFunction -> checkPrintlnArguments(node)
        }
        return violations
    }

    private fun checkPrintlnArguments(printFunction: PrintFunction) {
        val value: OptionalExpression = printFunction.value
        when (value) {
            is OptionalExpression.NoExpression -> {return}
            is OptionalExpression.HasExpression -> {
                val expression: Expression = value.expression
                if (expression is BinaryExpression) violations
                    .add(LinterViolation(
                        "println() should only be called with identifiers or literals, not expressions",
                    expression.range))
            } //tambien puedo crear un mapa (osea recibirlo) y entonces hacerlo MAS extensible y marcar en el mapa los nodos que quiero que me rompan esto
        }
    }
}