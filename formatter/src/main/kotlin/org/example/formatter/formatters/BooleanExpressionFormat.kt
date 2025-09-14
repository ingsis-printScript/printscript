package org.example.formatter.formatters

import org.example.formatter.Rule
import org.example.ast.ASTNode
import org.example.ast.expressions.BooleanExpression

class BooleanExpressionFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is BooleanExpression


    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        TODO("Not yet implemented")
    }
}
