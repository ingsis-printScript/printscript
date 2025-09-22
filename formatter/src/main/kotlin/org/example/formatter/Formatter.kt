package org.example.formatter

import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator
import org.example.common.results.Error
import org.example.common.results.NoResult
import org.example.common.results.Success
import org.example.token.Token
import org.example.formatter.formatters.ASTFormat
import java.io.File
import java.io.InputStream
import java.io.Writer

class Formatter(
    private val iterator: PrintScriptIterator<Token>, //o tokenbuffer?
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
        println("RULES ACTIVAS: " + ruler.allRules().mapValues { it.value })
        astFormat.formatNode(node, writer, rules, nestingLevel, PrivateIterator(nodes))
    }

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
