package org.example.formatter.providers

import org.example.common.PrintScriptIterator
import org.example.common.configuration.readers.ConfigurationReader
import org.example.common.configuration.readers.mappers.JsonMapper
import org.example.common.configuration.readers.mappers.YamlMapper
import org.example.formatter.Formatter
import org.example.formatter.rules.LineAfterSemicolonRule
import org.example.formatter.rules.LinesAfterPrintRule
import org.example.formatter.rules.NoSpaceAroundEqualsRule
import org.example.formatter.rules.PreserveOriginalSpaceRule
import org.example.formatter.rules.SpaceAroundColonRule
import org.example.formatter.rules.SpaceAroundEqualsRule
import org.example.formatter.rules.SpaceAroundEveryTokenRule
import org.example.formatter.rules.SpaceAroundOperatorRule
import org.example.token.Token
import java.io.InputStream
import java.io.Writer

class FormatterProvider10() : FormatterProvider {

    override fun provide(nodes: PrintScriptIterator<Token>, writer: Writer, inputStream: InputStream): Formatter {
        val otherRules = listOf(
            NoSpaceAroundEqualsRule(),
            SpaceAroundColonRule(),
            SpaceAroundEqualsRule(),
            SpaceAroundOperatorRule()
        )

        val rules = listOf(
            PreserveOriginalSpaceRule(),
            SpaceAroundOperatorRule(),
            NoSpaceAroundEqualsRule(),
            LineAfterSemicolonRule(),
            SpaceAroundColonRule(),
            SpaceAroundEqualsRule(),
            SpaceAroundEveryTokenRule(otherRules),
            LinesAfterPrintRule()
        )

        val configurationReader = ConfigurationReader(listOf(JsonMapper(), YamlMapper()))

        return Formatter(nodes, rules, configurationReader, inputStream, writer)
    }
}
