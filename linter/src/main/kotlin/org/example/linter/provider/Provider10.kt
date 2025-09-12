package org.example.linter.provider

import org.example.linter.Linter
import org.example.linter.configurationreaders.ConfigurationReader
import org.example.linter.configurationreaders.mappers.JsonMapper
import org.example.linter.configurationreaders.mappers.YamlMapper
import org.example.linter.rules.rulefactory.PrintArgumentRuleFactory
import org.example.linter.rules.rulefactory.SymbolFormatRuleFactory

class Provider10: Provider {
    override fun provide(): Linter {

        val ruleFactories = listOf(PrintArgumentRuleFactory(), SymbolFormatRuleFactory())
        val configurationReader = ConfigurationReader(listOf(JsonMapper(), YamlMapper()))

        val linter = Linter(ruleFactories, configurationReader)
        return linter
    }
}