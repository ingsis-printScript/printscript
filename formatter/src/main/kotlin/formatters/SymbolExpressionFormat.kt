package formatters

import Rule
import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression
import org.example.ast.visitors.ASTVisitor

class SymbolExpressionFormat: ASTFormat {


    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val symbolExpr = node as SymbolExpression
        result.append(symbolExpr.value)    }
}