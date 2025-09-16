package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.formatter.PrivateIterator
import org.example.formatter.Rule
import java.io.Writer

class BinaryExpressionFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is BinaryExpression

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int,
        context: PrivateIterator
    ) {
        val expr = node as BinaryExpression

        // formateo left expr
        ExpressionFormatterHelper().formatExpression(expr.left, writer, rules, nestingLevel, context)

        // operador con espacio
        writer.write(" ${expr.operator} ")

        // formateo right expr
        ExpressionFormatterHelper().formatExpression(expr.right, writer, rules, nestingLevel, context)
    }
}
