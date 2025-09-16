package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.ReadEnvExpression
import org.example.formatter.Rule
import java.io.Writer

class ReadEnvExpressionFormat : ASTFormat {
    override fun canHandle(node: ASTNode): Boolean = node is ReadEnvExpression

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        TODO("Not yet implemented")
    }
}