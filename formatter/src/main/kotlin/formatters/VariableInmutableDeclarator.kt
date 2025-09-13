package formatters

import Rule
import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.statements.VariableDeclarator

class VariableInmutableDeclarator : ASTFormat {

    override fun canHandle(node: ASTNode) = node is VariableInmutableDeclarator

    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        TODO("Not yet implemented")
    }
}
