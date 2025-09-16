package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.formatter.Rule
import java.io.Writer

class BinaryExpressionFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is BinaryExpression

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val expr = node as BinaryExpression

        // chequeo de espacios configurables
        val spaceRule = rules["spacesAroundOperators"]?.rule ?: true
        val space = if (spaceRule) " " else ""

        // formateo left expr
        ExpressionFormatterHelper().formatExpression(expr.left, writer, rules, nestingLevel)

        // operador con o sin espacio
        writer.write("$space${expr.operator}$space")

        // formateo right expr
        ExpressionFormatterHelper().formatExpression(expr.right, writer, rules, nestingLevel)
    }
}
