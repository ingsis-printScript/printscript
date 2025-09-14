package org.example.formatter.formatters

import org.example.formatter.Rule
import org.example.ast.ASTNode
import org.example.ast.statements.VariableDeclarator

class VariableDeclaratorFormat : ASTFormat {


    override fun canHandle(node: ASTNode) = node is VariableDeclarator


    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val declarator = node as VariableDeclarator

        // Indentación según nesting level
        val indentQty = rules["indentation"]?.quantity ?: 0
        repeat(nestingLevel * indentQty) { result.append(" ") }

        // Escribir tipo y nombre
        result.append(declarator.type.name) // asumiendo que Type tiene .name
        result.append(" ")
        result.append(declarator.symbol.value)

        // Si hay valor, agregar '=' y formatearlo
        declarator.value.let { expr ->
            val space = if (rules["spacesAroundAssign"]?.rule == true) " " else ""
            result.append("$space=$space")
            result.append(expr.toString())
        }

        result.append("\n")
    }
}
