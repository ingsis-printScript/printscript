package formatters

import Rule
import org.example.ast.ASTNode

class VariableInmutableDeclarator: ASTFormat {
    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        TODO("Not yet implemented")
    }
}