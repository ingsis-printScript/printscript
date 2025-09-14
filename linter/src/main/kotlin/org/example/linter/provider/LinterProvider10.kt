package org.example.linter.provider

import org.example.ast.expressions.BinaryExpression
import org.example.common.enums.SymbolFormat
import org.example.linter.Linter
import org.example.linter.configurationreaders.ConfigurationReader
import org.example.linter.configurationreaders.mappers.JsonMapper
import org.example.linter.configurationreaders.mappers.YamlMapper
import org.example.linter.rules.PrintArgumentRule
import org.example.linter.rules.SymbolFormatRule
import org.example.linter.rules.symbolformat.CamelCaseChecker
import org.example.linter.rules.symbolformat.SnakeCaseChecker

class LinterProvider10 : LinterProvider {
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

        val linter = Linter(rules, configurationReader)
        return linter
    }
}
