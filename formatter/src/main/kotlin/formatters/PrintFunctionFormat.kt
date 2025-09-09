package formatters

import Rule
import org.example.ast.ASTNode
import org.example.ast.statements.functions.PrintFunction

class PrintFunctionFormat: ASTFormat {

    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val printFunc = node as PrintFunction
        result.append("println(")

        printFunc.value.let { expr ->
            result.append(expr.toString())
        }

        result.append(")")
    }
}