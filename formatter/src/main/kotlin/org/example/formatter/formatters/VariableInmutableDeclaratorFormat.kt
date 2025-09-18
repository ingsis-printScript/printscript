package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.formatter.PrivateIterator
import org.example.formatter.Rule
import java.io.Writer

class VariableInmutableDeclaratorFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is VariableImmutableDeclarator

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int,
        context: PrivateIterator
    ) {
        val declarator = node as VariableImmutableDeclarator

        // Indentación segun rule
        val leftColonSpace = if (
            rules["enforce-spacing-before-colon-in-declaration"]?.rule
                ?: rules["enforce-spacing-around-colon"]?.rule
                ?: false
        ) " " else ""

        val rightColonSpace = if (
            rules["enforce-spacing-after-colon-in-declaration"]?.rule
                ?: rules["enforce-spacing-around-colon"]?.rule
                ?: true
        ) " " else ""

        val aroundEqFallback = rules["enforce-spacing-around-equals"]?.rule
            ?: rules["spacesAroundOperators"]?.rule
            ?: true

        val leftEqualSpace = if (
            rules["enforce-spacing-before-equals-in-declaration"]?.rule
                ?: aroundEqFallback
        ) " " else ""

        val rightEqualSpace = if (
            rules["enforce-spacing-after-equals-in-declaration"]?.rule
                ?: aroundEqFallback
        ) " " else ""

        // Escribir tipo y nombre
        writer.write("const ")
        writer.write(declarator.symbol.value)
        writer.write(leftColonSpace)
        writer.write(":")
        writer.write(rightColonSpace)
        writer.write(declarator.type.name.lowercase()) // asumiendo que Type tiene .name

        if (declarator.value is OptionalExpression.HasExpression) {
            writer.write(leftEqualSpace)
            writer.write("=")
            writer.write(rightEqualSpace)
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
