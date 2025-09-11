package formatters

import Rule
import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression

class BinaryExpressionFormat : ASTFormat {

    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val expr = node as BinaryExpression

        // chequeo de espacios configurables
        val spaceRule = rules["spacesAroundOperators"]?.rule ?: true
        val space = if (spaceRule) " " else ""

        // formateo left expr
        result.append(expr.left.toString())

        // operador con o sin espacio
        result.append("$space${expr.operator}$space")

        // formateo right expr
        result.append(expr.right.toString())
    }
}
