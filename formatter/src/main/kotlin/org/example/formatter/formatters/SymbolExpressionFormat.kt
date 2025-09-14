package org.example.formatter.formatters

import org.example.formatter.Rule
import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression

class SymbolExpressionFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is SymbolExpression

    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val symbolExpr = node as SymbolExpression
        result.append(symbolExpr.value)
    }
}
