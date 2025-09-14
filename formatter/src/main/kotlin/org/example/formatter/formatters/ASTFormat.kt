package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.formatter.Rule

interface ASTFormat {

    fun canHandle(node: ASTNode): Boolean

    fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int = 0
    )
}
