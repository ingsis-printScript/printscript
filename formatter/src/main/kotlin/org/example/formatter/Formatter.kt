package org.example.formatter

import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator
import org.example.formatter.formatters.ASTFormat
import java.io.StringWriter

class Formatter(
    private val rules: Map<String, Rule>,
    private val nodes: PrintScriptIterator<ASTNode>,
    private val writer: StringWriter,
    private val astFormat: ASTFormat
) : PrintScriptIterator<Unit> {
    fun format(): String {
        while (nodes.hasNext()) {
            val node = nodes.getNext()
            formatNode(node, writer, 0)
        }
        return writer.toString()
    }

    fun formatNode(node: ASTNode, writer: StringWriter, nestingLevel: Int = 0) {
        astFormat.formatNode(node, writer, rules, nestingLevel)
    }

    // standard rules I'll probably have to use in most formatters, I made them private functions
    private fun checkNewLines(): String {
        val rule = rules["newLines"]
        return if (rule?.rule == true) "\n" else ""
    }

    private fun checkSpaces(nestingLevel: Int): String {
        val rule = rules["spaces"]
        return if (rule?.rule == true) {
            val qty = rule.quantity ?: 4
            " ".repeat(nestingLevel * qty)
        } else {
            ""
        }
    }

    private fun checkRules(ruleName: String, append: String): String {
        val rule = rules[ruleName]
        return if (rule?.rule == true) append else ""
    }

    override fun hasNext(): Boolean {
        return nodes.hasNext()
    }

    override fun getNext() {
        formatNode(nodes.getNext(), writer, )
    }
}
