package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression
import org.example.formatter.PrivateIterator
import org.example.formatter.Rule
import java.io.Writer

class SymbolExpressionFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is SymbolExpression

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int,
        context: PrivateIterator
    ) {
        val symbolExpr = node as SymbolExpression
        writer.write(symbolExpr.value)
    }
}
