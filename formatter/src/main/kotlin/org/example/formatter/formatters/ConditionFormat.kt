package org.example.formatter.formatters

import org.example.ast.ASTNode
import org.example.ast.statements.Condition
import org.example.formatter.Rule
import java.io.Writer

class ConditionFormat : ASTFormat {

    override fun canHandle(node: ASTNode): Boolean = node is Condition

    override fun formatNode(
        node: ASTNode,
        writer: Writer,
        rules: Map<String, Rule>,
        nestingLevel: Int
    ) {
        if (node !is Condition) return

        val spaces = " ".repeat(nestingLevel * (rules["spaces"]?.quantity ?: 4))
        val newLine = if (rules["newLines"]?.rule == true) "\n" else ""

        writer.append("${spaces}if (${node.condition}) {$newLine")

        node.ifBlock.forEach { child ->
            val childSpaces = " ".repeat((nestingLevel + 1) * (rules["spaces"]?.quantity ?: 4))
            if (child is Condition) {
                // recursión
                formatNode(child, writer, rules, nestingLevel + 1)
            } else {
                writer.append(childSpaces)
                writer.append(child.toString() + newLine)
            }
        }

        writer.append("${spaces}}")

        node.elseBlock?.let { elseBlock ->
            writer.append(" else {$newLine")
            elseBlock.forEach { child ->
                val childSpaces = " ".repeat((nestingLevel + 1) * (rules["spaces"]?.quantity ?: 4))
                if (child is Condition) {
                    formatNode(child, writer, rules, nestingLevel + 1)
                } else {
                    writer.append(childSpaces)
                    writer.append(child.toString() + newLine)
                }
            }
            writer.append("${spaces}}$newLine")
        } ?: writer.append(newLine)
    }
}
