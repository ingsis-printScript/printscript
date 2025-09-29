import org.example.common.Position
import org.example.token.Token
import org.example.token.TokenType

class TokenFactory(
    startLine: Int = 1,
    startCol: Int = 1
) {
    private var line: Int = startLine
    private var col: Int = startCol
    private val tokens = mutableListOf<Token>()

    /** Inserta un salto de línea “físico” (próximo token irá a (line+1, col=1)). */
    fun nl(times: Int = 1): TokenFactory {
        repeat(times) {
            line += 1
            col = 1
        }
        return this
    }

    fun sp(n: Int = 1): TokenFactory { // inserta n espacios “originales”
        require(n >= 0)
        col += n
        return this
    }


    fun kw(value: String, sameLine: Boolean = true) = add(TokenType.KEYWORD, value, sameLine)
    fun sym(value: String, sameLine: Boolean = true) = add(TokenType.SYMBOL, value, sameLine)
    fun num(value: String, sameLine: Boolean = true) = add(TokenType.NUMBER, value, sameLine)
    fun str(value: String, sameLine: Boolean = true) = add(TokenType.STRING, value, sameLine)
    fun op(value: String, sameLine: Boolean = true) = add(TokenType.OPERATOR, value, sameLine)
    fun punct(value: String, sameLine: Boolean = true) = add(TokenType.PUNCTUATION, value, sameLine)
    fun paren(value: String, sameLine: Boolean = true) = punct(value, sameLine)

    /** Devuelve una copia inmutable de los tokens acumulados. */
    fun build(): List<Token> = tokens.toList()

    /** Limpia el buffer para reusar la misma instancia. */
    fun reset(startLine: Int = 1, startCol: Int = 1): TokenFactory {
        tokens.clear()
        line = startLine
        col = startCol
        return this
    }

    // -------- helpers “idiomáticos” para casos comunes en tests --------

    /** println("x"); */
    fun printlnLiteral(value: String = "\"x\""): TokenFactory {
        return sym("println")
            .paren("(")
            .str(value)
            .paren(")")
            .punct(";")
    }

    /** a=1; (o con espacios según tus reglas/posiciones) */
    fun assign(name: String, value: String, isNumber: Boolean = true): TokenFactory {
        sym(name)
        punct("=")
        if (isNumber) num(value) else str(value)
        punct(";")
        return this
    }

    /** f(1,2); */
    fun call2(fn: String, arg1: String, arg2: String, arg2IsNum: Boolean = true): TokenFactory {
        sym(fn).paren("(")
        num(arg1)
        punct(",")
        if (arg2IsNum) num(arg2) else str(arg2)
        paren(")")
        punct(";")
        return this
    }

    // ---------------------- privados ----------------------

    private fun add(type: TokenType, value: String, sameLine: Boolean): TokenFactory {
        if (!sameLine) nl()
        val pos = Position(line, col)
        tokens += Token(type = type, value = value, position = pos)
        col += value.length
        return this
    }
}
