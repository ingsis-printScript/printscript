import org.example.common.configuration.RulesConfiguration
import org.example.formatter.FormatterContext
import org.example.formatter.rules.Rule
import org.example.token.Token
import org.example.token.TokenType

class IfIndentRule : Rule {
    private var awaitingBraceForIf = false
    private var indentLevel = 0

    override fun isEnabled(configuration: RulesConfiguration): Boolean = true

    override fun before(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        val size = indentSize(ctx.configuration)

        // Si vemos '}', dedentear ANTES de imprimir esa línea
        if (isRBrace(cur)) indentLevel = (indentLevel - 1).coerceAtLeast(0)

        // Fijar indent de la línea actual
        ctx.setPendingIndentSpaces(indentLevel * size)

        // Es if?
        if (isIf(cur)) awaitingBraceForIf = true
    }

    override fun after(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        // Cuando aparece la '{' que abre el bloque del if, incrementar para próximas líneas
        if (awaitingBraceForIf && isLBrace(cur)) {
            indentLevel += 1
            awaitingBraceForIf = false
        }
    }

    private fun indentSize(cfg: RulesConfiguration) =
        cfg.getInt("indent-inside-if", 2)

    private fun isIf(t: Token?) = t?.type == TokenType.KEYWORD && t.value == "if"
    private fun isLBrace(t: Token?) = t?.type == TokenType.PUNCTUATION && t.value == "{"
    private fun isRBrace(t: Token?) = t?.type == TokenType.PUNCTUATION && t.value == "}"
}
