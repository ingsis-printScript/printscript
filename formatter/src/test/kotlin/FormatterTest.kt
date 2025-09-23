import org.example.common.PrintScriptIterator
import org.example.formatter.Formatter
import org.example.formatter.providers.FormatterProvider10
import org.example.token.Token
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.StringWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatterTest {

    // Iterador simple de tokens (calcado al estilo de LinterTest)
    private class TokenIterator(private val items: List<Token>) : PrintScriptIterator<Token> {
        private var i = 0
        override fun hasNext() = i < items.size
        override fun getNext(): Token = items[i++]
    }

    private fun configStream(
        mandatoryLineBreakAfterStatement: Boolean? = null,
        lineBreaksAfterPrintln: Int? = null,
        enforceNoSpacingAroundEquals: Boolean? = null,
        enforceSpacingAroundEquals: Boolean? = null,
        enforceSpacingBeforeEquals: Boolean? = null,
        enforceSpacingAfterEquals: Boolean? = null,
        enforceSpacingBeforeColon: Boolean? = null,
        enforceSpacingAfterColon: Boolean? = null,
        mandatorySingleSpaceSeparation: Boolean? = null,
        mandatorySpaceSurroundingOperations: Boolean? = null,
        // flags auxiliares que tu SpaceAroundEveryTokenRule consulta
        spaceBeforeColonFlag: Boolean? = null,
        spaceAfterColonFlag: Boolean? = null,
    ): InputStream {
        val parts = mutableListOf<String>()
        fun addBool(k: String, v: Boolean?) { if (v != null) parts += "\"$k\": $v" }
        fun addInt(k: String, v: Int?) { if (v != null) parts += "\"$k\": $v" }

        addBool("mandatory-line-break-after-statement", mandatoryLineBreakAfterStatement)
        addInt("line-breaks-after-println", lineBreaksAfterPrintln)
        addBool("enforce-no-spacing-around-equals", enforceNoSpacingAroundEquals)
        addBool("enforce-spacing-around-equals", enforceSpacingAroundEquals)
        addBool("enforce-spacing-before-equals", enforceSpacingBeforeEquals)
        addBool("enforce-spacing-after-equals", enforceSpacingAfterEquals)
        addBool("enforce-spacing-before-colon-in-declaration", enforceSpacingBeforeColon)
        addBool("enforce-spacing-after-colon-in-declaration", enforceSpacingAfterColon)
        addBool("mandatory-single-space-separation", mandatorySingleSpaceSeparation)
        addBool("mandatory-space-surrounding-operations", mandatorySpaceSurroundingOperations)
        // usadas por SpaceAroundEveryTokenRule
        addBool("spaceBeforeColon", spaceBeforeColonFlag)
        addBool("spaceAfterColon", spaceAfterColonFlag)

        val json = "{${parts.joinToString(",")}}"
        return ByteArrayInputStream(json.toByteArray())
    }

    // ===== helper para construir el Formatter usando tu Provider 1.0 =====
    private fun makeFormatterWithProvider(tokens: List<Token>, cfgStream: InputStream): Pair<Formatter, StringWriter> {
        val it = TokenIterator(tokens)
        val out = StringWriter()
        val provider = FormatterProvider10()
        val fmt = provider.provide(it, out, cfgStream)
        return fmt to out
    }

    // ===================== TESTS =====================

    @Test
    fun `no spaces around '=' (override gap original)`() {
        val tokens = TokenFactory()
            .kw("let").sp().sym("a").sp().punct("=").sp().num("1").punct(";")
            .build()

        val cfg = configStream(
            enforceNoSpacingAroundEquals = true
        )

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("let a=1;", out.toString())
    }

    @Test
    fun `space around '=' habilitada`() {
        val tokens = TokenFactory()
            .kw("let").sp().sym("a").sp().punct("=").sp().num("1").punct(";")
            .build()

        val cfg = configStream(enforceSpacingAroundEquals = true)

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("let a = 1;", out.toString())
    }

    @Test
    fun `colon spacing - only after`() {
        // let something:string="x"; -> "let something: string=\"x\";"
        val tokens = TokenFactory()
            .kw("let").sp().sym("something").sp().punct(":").sym("string").sp()
            .punct("=").sp().str("\"x\"").punct(";")
            .build()

        val cfg = configStream(
            enforceSpacingBeforeColon = false,
            enforceSpacingAfterColon = true
        )

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("let something: string = \"x\";", out.toString())
    }

    @Test
    fun `colon spacing - only before`() {
        // -> "let something :string=\"x\";"
        val tokens = TokenFactory()
            .kw("let").sp().sym("something").sp().punct(":").sp().sym("string").sp()
            .punct("=").sp().str("\"x\"").punct(";")
            .build()

        val cfg = configStream(
            enforceSpacingBeforeColon = true,
            enforceSpacingAfterColon = false
        )

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("let something :string = \"x\";", out.toString())
    }

    @Test
    fun `colon spacing - before and after`() {
        val tokens = TokenFactory()
            .kw("let").sp().sym("x").sp().punct(":").sym("number")
            .punct("=").num("1").punct(";")
            .build()

        val cfg = configStream(
            enforceSpacingBeforeColon = true,
            enforceSpacingAfterColon = true
        )

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("let x : number=1;", out.toString())
    }

    @Test
    fun `newline after 'Semicolon' entre statements`() {
        // a=1; b=2; -> "a=1;\nb=2;"
        val tokens = TokenFactory()
            .sym("a").punct("=").num("1").punct(";")
            .sym("b").punct("=").num("2").punct(";")
            .build()

        val cfg = configStream(mandatoryLineBreakAfterStatement = true)

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("a=1;\nb=2;", out.toString())
    }

    @Test
    fun `println con 2 blank lines luego del 'Semicolon' (si no es EOF)`() {
        val tokens = TokenFactory()
            .printlnLiteral("\"x\"")
            .kw("let").sp().sym("a").punct("=").num("1").punct(";")
            .build()

        val cfg = configStream(lineBreaksAfterPrintln = 2)

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("println(\"x\");\n\n\nlet a=1;", out.toString())
    }



    @Test
    fun `espacios alrededor de operadores binarios`() {
        // 1+2; -> "1 + 2;"
        val tokens = TokenFactory()
            .num("1").op("+").num("2").punct(";")
            .build()

        val cfg = configStream(mandatorySpaceSurroundingOperations = true)

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("1 + 2;", out.toString())
    }


    @Test
    fun `precedencia - no spacing around '=' gana sobre single-space separation`() {
        val tokens = TokenFactory()
            .sym("a").punct("=").num("1").punct(";")
            .build()

        val cfg = configStream(
            enforceNoSpacingAroundEquals = true,
            mandatorySingleSpaceSeparation = true,
            spaceBeforeColonFlag = false,
            spaceAfterColonFlag = false
        )

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("a=1;", out.toString())
    }

    @Test
    fun `EOF - LinesAfterPrintRule no agrega saltos si no hay siguiente token`() {
        // println("x"); EOF -> no agrega \n extra
        val tokens = TokenFactory()
            .printlnLiteral("\"x\"")
            .build()

        val cfg = configStream(lineBreaksAfterPrintln = 2)

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("println(\"x\");", out.toString())
    }

    @Test
    fun `single-space respeta flags de colon - solo BEFORE `() {
        val tokens = TokenFactory()
            .kw("let").sym("x").punct(":").sym("number").punct(";") // sin espacios originales
            .build()

        val cfg = configStream(
            mandatorySingleSpaceSeparation = true,
            spaceBeforeColonFlag = true,   // permitir espacio antes de ':'
        )

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("let x : number;", out.toString())
    }

    @Test
    fun `operadores tienen prioridad sobre single-space (no duplica ni pisa)`() {
        val tokens = TokenFactory()
            .num("1").op("+").num("2").punct(";")
            .build()

        val cfg = configStream(
            mandatorySpaceSurroundingOperations = true, // activa regla de operadores
            mandatorySingleSpaceSeparation = true       // activa "every" como relleno
        )

        val (fmt, out) = makeFormatterWithProvider(tokens, cfg)
        fmt.getNext()
        assertEquals("1 + 2;", out.toString())
    }



}
