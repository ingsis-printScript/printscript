package formatters

import Rule
import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.NumberExpression

class NumberExpressionFormat : ASTFormat {


    override fun canHandle(node: ASTNode) = node is NumberExpression


    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val numberExpr = node as NumberExpression
        result.append(numberExpr.value)
    }
}
