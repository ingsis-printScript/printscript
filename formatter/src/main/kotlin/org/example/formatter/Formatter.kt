package org.example.formatter

import org.example.common.PrintScriptIterator
import org.example.common.configuration.RulesConfiguration
import org.example.common.configuration.configurationreaders.ConfigurationReader
import org.example.formatter.rules.Rule
import org.example.token.Token
import java.io.File
import java.io.InputStream
import java.io.Writer

class Formatter(
    private val iterator: PrintScriptIterator<Token>,
    private val rules: List<Rule>,
    private val configurationReader: ConfigurationReader,
    private val configInputStream: InputStream,
    private val writer: Writer
) : PrintScriptIterator<Unit> {

    private val configuration: RulesConfiguration by lazy {
        val tmp = createTempConfigFile(configInputStream)
        val data = configurationReader.read(tmp.absolutePath)
        tmp.delete()
        RulesConfiguration(data)
    }

    private val activeRules: List<Rule> by lazy {
        rules.filter { it.isEnabled(configuration) }
    }

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun getNext() {
        formatAll()
    }

    fun formatAll() {
        val ctx = FormatterContext(writer, configuration)
        var lookahead: Token? = null

        fun nextOrNull(): Token? {
            if (lookahead != null) return lookahead.also { lookahead = null }
            return if (iterator.hasNext()) iterator.getNext() else null
        }

        fun peekOrNull(): Token? {
            if (lookahead == null && iterator.hasNext()) lookahead = iterator.getNext()
            return lookahead
        }

        var prev: Token? = null
        var cur: Token? = nextOrNull()

        while (true) {
            val current = cur ?: break
            val next = peekOrNull()

            // sin lambdas -> sin clausuras
            for (rule in activeRules) rule.before(prev, current, next, ctx)

            ctx.flushPendingGap()

            ctx.writeRaw(current.value)

            for (rule in activeRules) rule.after(prev, current, next, ctx)

            prev = current
            cur = nextOrNull()
        }
    }

    private fun createTempConfigFile(configInputStream: InputStream): File {
        val tempFile = File.createTempFile("formatter_config", ".json")
        tempFile.writeBytes(configInputStream.readBytes())
        return tempFile
    }
}
