package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableDeclarator
import org.example.formatter.PrivateIterator
import org.example.formatter.Rule
import java.io.Writer

class VariableDeclaratorFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is VariableDeclarator

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int,
        context: PrivateIterator
    ) {
        val declarator = node as VariableDeclarator

        // Indentación según rule
        val leftColonSpace = if (rules["enforce-spacing-before-colon-in-declaration"]?.rule == true) " " else ""
        val rightColonSpace = if (rules["enforce-spacing-after-colon-in-declaration"]?.rule == true) " " else ""
        val arroundColonSpace = if (rules["enforce-spacing-around-colon"]?.rule == true) " " else ""
        val leftEqualSpace = if (rules["enforce-spacing-before-equals-in-declaration"]?.rule == true) " " else ""
        val rightEqualSpace = if (rules["enforce-spacing-after-equals-in-declaration"]?.rule == true) " " else ""
        val arroundEqualSpace = if (rules["enforce-spacing-around-equals"]?.rule == true) " " else ""

        // Escribir tipo y nombre
        writer.write("let ")
        writer.write(declarator.symbol.value)
        writer.write(leftColonSpace + arroundColonSpace)
        writer.write(":")
        writer.write(rightColonSpace + arroundColonSpace)
        writer.write(declarator.type.name.lowercase()) // asumiendo que Type tiene .name
        if (declarator.value is OptionalExpression.HasExpression) {
            writer.write(leftEqualSpace + arroundEqualSpace)
            writer.write("=")
            writer.write(rightEqualSpace + arroundEqualSpace)
        }

        // Si hay valor, agregar '=' y formatearlo
        declarator.value.let { expr ->
            if (expr is OptionalExpression.HasExpression) {
                ExpressionFormatterHelper().formatExpression(expr.expression, writer, rules, nestingLevel, context)
            }
        }
        writer.write(";")
        if (context.hasNext()) writer.write("\n")
    }
}
