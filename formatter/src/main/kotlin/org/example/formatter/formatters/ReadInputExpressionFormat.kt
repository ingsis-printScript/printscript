package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.ReadInputExpression
import org.example.formatter.PrivateIterator
import org.example.formatter.Rule
import java.io.Writer

class ReadInputExpressionFormat : ASTFormat {

    override fun canHandle(node: ASTNode): Boolean = node is ReadInputExpression

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int,
        context: PrivateIterator
    ) {
        if (node !is ReadInputExpression) return

        writer.append("readInput(")
        if (node.value is OptionalExpression.HasExpression) {
            ExpressionFormatterHelper().formatExpression((node.value as OptionalExpression.HasExpression).expression, writer, rules, nestingLevel, context)
        }
    }
}
