package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.statements.functions.PrintFunction
import org.example.formatter.Rule
import java.io.Writer

class PrintFunctionFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is PrintFunction

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val printFunc = node as PrintFunction
        val result = StringBuilder()
        result.append("println(")

        printFunc.value.let { expr ->
            result.append(expr.toString())
        }

        result.append(")")
        writer.write(result.toString())
    }
}
