package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.StringExpression
import org.example.formatter.Rule

class BooleanExpressionFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is BooleanExpression

    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val boolExpr = node as BooleanExpression

        result.append(boolExpr.value)
    }
}
