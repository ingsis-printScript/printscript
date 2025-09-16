package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.formatter.Rule
import java.io.Writer

class VariableInmutableDeclaratorFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is VariableImmutableDeclarator

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val declarator = node as VariableDeclarator

        // Indentación según nesting level
        val indentQty = rules["indentation"]?.quantity ?: 0
        repeat(nestingLevel * indentQty) { writer.write(" ") }

        // Escribir tipo y nombre
        writer.write(declarator.type.name) // asumiendo que Type tiene .name
        writer.write(" ")
        writer.write(declarator.symbol.value)

        // Si hay valor, agregar '=' y formatearlo
        declarator.value.let { expr ->
            if (expr is OptionalExpression.HasExpression){
                ExpressionFormatterHelper().formatExpression(expr.expression, writer, rules, nestingLevel)
            }
        }

        writer.write("\n")
    }
}
