package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.formatter.Rule
import java.io.Writer

class VariableInmutableDeclaratorFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is VariableImmutableDeclarator

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        TODO("Not yet implemented")
    }
}
