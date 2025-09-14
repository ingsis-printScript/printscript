package formatters

import Rule
import org.example.ast.ASTNode
import org.example.ast.statements.VariableImmutableDeclarator

class VariableInmutableDeclaratorFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is VariableImmutableDeclarator

    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        TODO("Not yet implemented")
    }
}
