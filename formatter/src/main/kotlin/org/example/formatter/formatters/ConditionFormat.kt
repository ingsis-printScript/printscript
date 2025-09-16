package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.statements.Condition
import org.example.formatter.Rule
import java.io.Writer

class ConditionFormat : ASTFormat {

    override fun canHandle(node: ASTNode): Boolean = node is Condition


    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {

    }
}