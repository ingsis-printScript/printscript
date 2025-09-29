import org.example.common.configuration.RulesConfiguration
import org.example.formatter.FormatterContext
import org.example.formatter.rules.ClaimsToken
import org.example.formatter.rules.Rule
import org.example.token.Token
import org.example.token.TokenType

class IfBracePlacementRule : Rule, ClaimsToken {

    private var awaitingIfBrace = false
    private var parenDepth = 0

    override fun isEnabled(configuration: RulesConfiguration): Boolean {
        val same  = configuration.getBoolean("if-brace-same-line")
        val below = configuration.getBoolean("if-brace-below-line")
        return same || below
    }

    override fun before(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {
        val same  = ctx.configuration.getBoolean("if-brace-same-line")
        val below = ctx.configuration.getBoolean("if-brace-below-line")

        if (isIf(cur)) {
            awaitingIfBrace = true
            parenDepth = 0
            return
        }
        if (awaitingIfBrace) {
            if (isOpenParen(cur)) parenDepth++
            if (isCloseParen(cur)) parenDepth = (parenDepth - 1).coerceAtLeast(0)
        }

        // Cuando la { abre el if (y ya cerramos paréntesis)
        if (awaitingIfBrace && parenDepth == 0 && isLBrace(cur)) {
            if (same) {
                ctx.setPendingNewlines(0)
                ctx.setPendingSpaces(1)
            } else if (below) {
                ctx.clearPendingSpaces()
                ctx.setPendingNewlines(1)
            }
            awaitingIfBrace = false
        }
    }

    override fun claims(prev: Token?, cur: Token?, next: Token?, cfg: RulesConfiguration): Boolean {
        if (!isEnabled(cfg)) return false
        return awaitingIfBrace && parenDepth == 0 &&
                ((isCloseParen(cur) && isLBrace(next)) || isLBrace(cur))
    }

    private fun isIf(t: Token?) =
        t != null && (t.type == TokenType.SYMBOL || t.type == TokenType.KEYWORD) && t.value == "if"
    private fun isOpenParen(t: Token?) = t?.type == TokenType.PUNCTUATION && t.value == "("
    private fun isCloseParen(t: Token?) = t?.type == TokenType.PUNCTUATION && t.value == ")"
    private fun isLBrace(t: Token?) = t?.type == TokenType.PUNCTUATION && t.value == "{"
}
