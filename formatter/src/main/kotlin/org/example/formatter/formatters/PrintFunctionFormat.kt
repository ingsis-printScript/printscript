package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
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

        val indentation = rules["line-breaks-after-println"]?.quantity ?: 0
        writer.write("println(")

        printFunc.value.let { expr ->
            if (expr is OptionalExpression.HasExpression) {
                ExpressionFormatterHelper().formatExpression(expr.expression, writer, rules, nestingLevel)
            }
        }

        writer.write(")")
        for (i in 0 until indentation) {
            writer.write("\n")
        }
    }
}
