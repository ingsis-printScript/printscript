package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.formatter.PrivateIterator
import org.example.formatter.Rule
import java.io.Writer

interface ASTFormat {

    fun canHandle(node: ASTNode): Boolean

    fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int = 0,
        context: PrivateIterator
    )
}
