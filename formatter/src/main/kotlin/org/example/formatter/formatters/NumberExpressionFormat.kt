package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.NumberExpression
import org.example.formatter.PrivateIterator
import org.example.formatter.Rule
import java.io.Writer

class NumberExpressionFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is NumberExpression

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int,
        context: PrivateIterator
    ) {
        val numberExpr = node as NumberExpression
        writer.write(numberExpr.value)
    }
}
