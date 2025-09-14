package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.StringExpression
import org.example.formatter.Rule

class StringExpressionFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is StringExpression

    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val stringExpr = node as StringExpression

        // Para incluir las comillas en la salida formateada
        result.append("\"${stringExpr.value}\"")
    }
}
