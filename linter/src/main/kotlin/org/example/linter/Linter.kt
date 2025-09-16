package org.example.linter

import org.example.ast.ASTNode
import org.example.common.ErrorHandler
import org.example.common.PrintScriptIterator
import org.example.common.results.Error
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.linter.configurationreaders.ConfigurationReader
import org.example.linter.rules.Rule
import java.io.File
import java.io.InputStream

class Linter(
    private val iterator: PrintScriptIterator<Result>,
    private val rules: List<Rule>,
    private val configurationReader: ConfigurationReader,
    private val configInputStream: InputStream,
    private val errorHandler: ErrorHandler
) : PrintScriptIterator<Result> {

    private val configuration: LinterConfiguration by lazy {
        val tempFile = createTempConfigFile(configInputStream)
        val configData = configurationReader.read(tempFile.absolutePath)
        tempFile.delete()
        LinterConfiguration(configData)
    }

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun getNext(): Result {
        val result = iterator.getNext()
        println("getNext:$result")
        if (result is Error) {
            errorHandler.handleError(result.message)
            return result
        }
        val node = result as Success<ASTNode>

        return analyze(node.value)
    }

    fun analyze(ast: ASTNode): Result {
        for (rule in rules) {
            val result = rule.check(ast, configuration, errorHandler)
            if (result is Error) {
                return result
            }
        }
        return Success(Unit)
    }

    private fun createTempConfigFile(configInputStream: InputStream): File {
        val tempFile = File.createTempFile("linter_config", ".json")
        tempFile.writeBytes(configInputStream.readBytes())
        return tempFile
    }
}
