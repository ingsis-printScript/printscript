package org.example.linter.provider

import org.example.common.enums.SymbolFormat
import org.example.linter.Linter
import org.example.linter.configurationreaders.ConfigurationReader
import org.example.linter.configurationreaders.mappers.JsonMapper
import org.example.linter.configurationreaders.mappers.YamlMapper
import org.example.linter.rules.rulefactory.PrintArgumentRuleFactory
import org.example.linter.rules.rulefactory.SymbolFormatRuleFactory
import org.example.linter.rules.symbolformat.CamelCaseChecker
import org.example.linter.rules.symbolformat.SnakeCaseChecker

class Provider10: Provider {
    override fun provide(): Linter {
        val symbolFormatCheckers = mapOf(
            SymbolFormat.CAMEL_CASE to CamelCaseChecker(),
            SymbolFormat.SNAKE_CASE to SnakeCaseChecker()
        )

        val ruleFactories = listOf(
            PrintArgumentRuleFactory(),
            SymbolFormatRuleFactory(symbolFormatCheckers)
        )
        val configurationReader = ConfigurationReader(listOf(JsonMapper(), YamlMapper()))

        val linter = Linter(ruleFactories, configurationReader)
        return linter
    }
}