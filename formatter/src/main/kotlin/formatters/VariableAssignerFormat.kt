package formatters

import Rule
import org.example.ast.ASTNode
import org.example.ast.statements.VariableAssigner

class VariableAssignerFormat: ASTFormat {

    override fun formatNode(
        node: ASTNode,
        result: StringBuilder,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        val assigner = node as VariableAssigner

        //Indentación según nesting level
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
    }
}