import formatters.ASTFormat
import formatters.BinaryExpressionFormat
import formatters.BooleanExpressionFormat
import formatters.NumberExpressionFormat
import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.NumberExpression
import java.text.Format

class CompositeASTFormat (
    private val formats: List<ASTFormat>
): ASTFormat {
    override fun canHandle(node: ASTNode): Boolean {
        TODO("Not yet implemented")
    }

    override fun formatNode(
        node: org.example.ast.ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        when (node) {
            is BinaryExpression -> formats.filterIsInstance<BinaryExpressionFormat>()
                .firstOrNull()?.formatNode(node, result, rules, nestingLevel)

            is BooleanExpression -> formats.filterIsInstance<BooleanExpressionFormat>().firstOrNull()?.formatNode(node, result, rules, nestingLevel)

            is NumberExpression -> formats.filterIsInstance<NumberExpressionFormat>().firstOrNull()?.formatNode(node, result, rules, nestingLevel)



            else -> throw IllegalArgumentException("No formatter found for node type: ${node::class.simpleName}")
        }
    }
}