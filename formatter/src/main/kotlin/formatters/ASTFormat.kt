package formatters

import Rule
import org.example.ast.ASTNode

interface ASTFormat {

    fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int = 0
    )
}
