package org.example.formatter

import org.example.formatter.formatters.ASTFormat
import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator

class Formatter(
    private val rules: Map<String, Rule>,
    private val nodes: PrintScriptIterator<ASTNode>,
    private val astFormat: ASTFormat
) {
    fun format(): String {
        val sb = StringBuilder()
        while (nodes.hasNext()) {
            val node = nodes.getNext()
            formatNode(node, sb, 0)
        }
        return sb.toString()
    }

    fun formatNode(node: ASTNode, sb: StringBuilder, nestingLevel: Int = 0) {
        astFormat.formatNode(node, sb, rules, nestingLevel)
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
}
