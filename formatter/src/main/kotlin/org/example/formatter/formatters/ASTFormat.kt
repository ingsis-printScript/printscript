package org.example.formatter.formatters

import org.example.formatter.Rule
import org.example.ast.ASTNode

interface ASTFormat {

    fun canHandle(node: ASTNode): Boolean

    fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int = 0
    )
}
