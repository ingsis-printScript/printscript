package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.statements.VariableAssigner
import org.example.formatter.Rule
import java.io.Writer

class VariableAssignerFormat : ASTFormat {

    override fun canHandle(node: ASTNode) = node is VariableAssigner

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val assigner = node as VariableAssigner
        val result = StringBuilder()

        // Indentación según nesting level
        val indentQty = rules["indentation"]?.quantity ?: 0
        repeat(nestingLevel * indentQty) { result.append(" ") }

        // Formateo del símbolo
        result.append(assigner.symbol.value)

        // Espacio + operador + espacio
        val space = if (rules["spacesAroundAssign"]?.rule == true) " " else ""
        result.append("$space=$space")

        // Formateo del valor si existe
        assigner.value.let { expr ->
            result.append(expr.toString())
        }

        result.append("\n")
        writer.write(result.toString())
    }
}
