package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.StringExpression
import org.example.formatter.Rule
import java.io.Writer

class StringExpressionFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is StringExpression

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val stringExpr = node as StringExpression

        writer.write(stringExpr.value)
    }
}
