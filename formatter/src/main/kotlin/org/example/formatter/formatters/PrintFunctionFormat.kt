package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.functions.PrintFunction
import org.example.formatter.PrivateIterator
import org.example.formatter.Rule
import java.io.Writer

class PrintFunctionFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is PrintFunction

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int,
        context: PrivateIterator
    ) {
        val printFunc = node as PrintFunction

        val indentation = rules["line-breaks-after-println"]?.quantity ?: 0
        writer.write("println(")

        printFunc.value.let { expr ->
            if (expr is OptionalExpression.HasExpression) {
                ExpressionFormatterHelper().formatExpression(expr.expression, writer, rules, nestingLevel, context)
            }
        }

        writer.write(")")
        writer.write(";")
        if (context.hasNext()) {
            for (i in 0 until indentation) {
                writer.write("\n")
            }
        }
    }
}