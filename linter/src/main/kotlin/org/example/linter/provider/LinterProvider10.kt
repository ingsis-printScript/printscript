package org.example.linter.provider

import org.example.ast.expressions.BinaryExpression
import org.example.common.ErrorHandler
import org.example.common.PrintScriptIterator
import org.example.common.enums.SymbolFormat
import org.example.common.results.Result
import org.example.linter.Linter
import org.example.linter.configurationreaders.ConfigurationReader
import org.example.linter.configurationreaders.mappers.JsonMapper
import org.example.linter.configurationreaders.mappers.YamlMapper
import org.example.linter.rules.functionargument.PrintArgumentRule
import org.example.linter.rules.symbolformat.SymbolFormatRule
import org.example.linter.rules.symbolformat.checker.CamelCaseChecker
import org.example.linter.rules.symbolformat.checker.SnakeCaseChecker
import java.io.InputStream

class LinterProvider10(private val iterator: PrintScriptIterator<Result>, private val inputStream: InputStream, private val errorHandler: ErrorHandler) : LinterProvider {
    override fun provide(): Linter {
        val prohibitedNodes = setOf(BinaryExpression::class)
        val symbolFormats = mapOf(
            SymbolFormat.CAMEL_CASE to CamelCaseChecker(),
            SymbolFormat.SNAKE_CASE to SnakeCaseChecker()
        )

        val rules = listOf(
            PrintArgumentRule(prohibitedNodes),
            SymbolFormatRule(symbolFormats)
        )
        val configurationReader = ConfigurationReader(listOf(JsonMapper(), YamlMapper()))

        val linter = Linter(iterator, rules, configurationReader, inputStream, errorHandler)
        return linter
    }
}
