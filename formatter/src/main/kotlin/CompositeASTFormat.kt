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
        // el composite "soporta" cualquier nodo que alguno de sus hijos soporte
        return formats.any { it.canHandle(node) }
    }

    override fun formatNode(
        node: org.example.ast.ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val formatter = formats.firstOrNull { it.canHandle(node) }
            ?: throw IllegalArgumentException("No formatter found for node type: ${node::class.simpleName}")

        formatter.formatNode(node, result, rules, nestingLevel)

    }
}