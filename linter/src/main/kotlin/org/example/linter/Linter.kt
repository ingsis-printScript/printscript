package org.example.linter

import org.example.ast.ASTNode
import org.example.common.ErrorHandler
import org.example.common.PrintScriptIterator
import org.example.linter.configurationreaders.ConfigurationReader
import org.example.linter.data.LinterViolation
import org.example.linter.rules.Rule
import org.example.common.results.Result
import org.example.common.results.Error
import org.example.common.results.Success
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
        if (result is Error) {
            errorHandler.handleError(result.message)
            return result
        }
        val node = result as Success<ASTNode>

        return analyze(node.value)
    }

    fun analyze(ast: ASTNode): Result {

        val violations = mutableListOf<LinterViolation>()
        for (rule in rules) {
            violations.addAll(rule.check(ast, configuration))
        }

        if (violations.isNotEmpty()) {
            for (violation in violations) {
                errorHandler.handleError(violation.message)
            }
            return Error("Linter found ${violations.size} violations.")
        }
        return Success(Unit)
    }

    private fun createTempConfigFile(configInputStream: InputStream): File {
        val tempFile = File.createTempFile("linter_config", ".json")
        tempFile.writeBytes(configInputStream.readBytes())
        return tempFile
    }
}
