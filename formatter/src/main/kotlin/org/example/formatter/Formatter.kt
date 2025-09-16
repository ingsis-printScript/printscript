package org.example.formatter

import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator
import org.example.common.results.Error
import org.example.common.results.NoResult
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.formatter.formatters.ASTFormat
import java.io.File
import java.io.InputStream
import java.io.Writer

class Formatter(
    private val nodes: PrintScriptIterator<Result>,
    private val writer: Writer,
    private val astFormat: ASTFormat,
    private val configInputStream: InputStream
) : PrintScriptIterator<Unit> {

    private val ruler: Ruler by lazy {
        val tempFile = createTempConfigFile(configInputStream)
        try {
            Ruler.fromJsonFile(tempFile.absolutePath)
        } finally {
            tempFile.delete()
        }
    }

    private val rules = ruler.allRules()

    fun formatNode(node: ASTNode, writer: Writer, nestingLevel: Int = 0) {
        astFormat.formatNode(node, writer, rules, nestingLevel, PrivateIterator(nodes))
    }

//    // standard rules I'll probably have to use in most formatters, I made them private functions
//    private fun checkNewLines(): String {
//        val rule = rules["newLines"]
//        return if (rule?.rule == true) "\n" else ""
//    }
//
//    private fun checkSpaces(nestingLevel: Int): String {
//        val rule = rules["spaces"]
//        return if (rule?.rule == true) {
//            val qty = rule.quantity ?: 4
//            " ".repeat(nestingLevel * qty)
//        } else {
//            ""
//        }
//    }
//
//    private fun checkRules(ruleName: String, append: String): String {
//        val rule = rules[ruleName]
//        return if (rule?.rule == true) append else ""
//    }

    private fun createTempConfigFile(configInputStream: InputStream): File {
        val tempFile = File.createTempFile("formatter_rules", ".json")
        configInputStream.use { input ->
            tempFile.outputStream().use { out ->
                input.copyTo(out)
            }
        }
        return tempFile
    }

    override fun hasNext(): Boolean {
        return nodes.hasNext()
    }

    override fun getNext() {
        when (val res = nodes.getNext()) {
            is Success<*> -> formatNode(res.value as ASTNode, writer, 0)
            is Error -> TODO()
            is NoResult -> TODO()
        }
    }
}
