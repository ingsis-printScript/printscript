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
        val result = StringBuilder()

        // chequeo de espacios configurables
        val spaceRule = rules["spacesAroundOperators"]?.rule ?: true
        val space = if (spaceRule) " " else ""

        // formateo left expr
        result.append(expr.left.toString())

        // operador con o sin espacio
        result.append("$space${expr.operator}$space")

        // formateo right expr
        result.append(expr.right.toString())
        writer.write(result.toString())
    }
}
