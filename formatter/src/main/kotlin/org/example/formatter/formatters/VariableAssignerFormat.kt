package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableAssigner
import org.example.formatter.PrivateIterator
import org.example.formatter.Rule
import java.io.Writer

class VariableAssignerFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is VariableAssigner

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int,
        context: PrivateIterator
    ) {
        val assigner = node as VariableAssigner

        // Indentación según rule
        val leftEqualSpace = if (rules["enforce-spacing-before-equals-in-declaration"]?.rule == true) " " else ""
        val rightEqualSpace = if (rules["enforce-spacing-after-equals-in-declaration"]?.rule == true) " " else ""
        val arroundEqualSpace = if (rules["enforce-spacing-around-equals"]?.rule == true) " " else ""

        // Formateo del símbolo
        writer.write(assigner.symbol.value)
        writer.write(leftEqualSpace + arroundEqualSpace)
        writer.write("=")
        writer.write(rightEqualSpace + arroundEqualSpace)

        // Formateo del valor si existe
        assigner.value.let { expr ->
            if (expr is OptionalExpression.HasExpression) {
                ExpressionFormatterHelper().formatExpression(expr.expression, writer, rules, nestingLevel, context)
            }
        }
        writer.write(";")
        if (context.hasNext()) writer.write("\n")
    }
}
