import org.example.common.PrintScriptIterator
import org.example.formatter.Formatter
import org.example.formatter.providers.FormatterVersionProvider
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
        spaceAfterColonFlag: Boolean? = null
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
    private fun makeFormatter(
        tokens: List<Token>,
        cfgStream: InputStream,
        version: String
    ): Pair<Formatter, StringWriter> {
        val it = TokenIterator(tokens)
        val out = StringWriter()
        val provider = FormatterVersionProvider().with(version)
        val fmt = provider.provide(it, out, cfgStream)
        return fmt to out
    }

    private fun makeFormatter10(tokens: List<Token>, cfg: InputStream) =
        makeFormatter(tokens, cfg, "1.0")

    private fun makeFormatter11(tokens: List<Token>, cfg: InputStream) =
        makeFormatter(tokens, cfg, "1.1")

    // ===================== TESTS =====================

    @Test
    fun `no spaces around '=' (override gap original)`() {
        val tokens = TokenFactory()
            .kw("let").sp().sym("a").sp().punct("=").sp().num("1").punct(";")
            .build()

        val cfg = configStream(
            enforceNoSpacingAroundEquals = true
        )

        val (fmt, out) = makeFormatter10(tokens, cfg)
        fmt.getNext()
        assertEquals("let a=1;", out.toString())
    }

    @Test
    fun `space around '=' habilitada`() {
        val tokens = TokenFactory()
            .kw("let").sp().sym("a").sp().punct("=").sp().num("1").punct(";")
            .build()

        val cfg = configStream(enforceSpacingAroundEquals = true)

        val (fmt, out) = makeFormatter10(tokens, cfg)
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

        val (fmt, out) = makeFormatter10(tokens, cfg)
        fmt.getNext()
        assertEquals("let something : string = \"x\";", out.toString())
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

        val (fmt, out) = makeFormatter10(tokens, cfg)
        fmt.getNext()
        assertEquals("let something : string = \"x\";", out.toString())
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

        val (fmt, out) = makeFormatter10(tokens, cfg)
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

        val (fmt, out) = makeFormatter10(tokens, cfg)
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

        val (fmt, out) = makeFormatter10(tokens, cfg)
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

        val (fmt, out) = makeFormatter10(tokens, cfg)
        fmt.getNext()
        assertEquals("1 + 2;", out.toString())
    }

    @Test
    fun `precedencia - no spacing around '=' gana sobre single-space separation`() {
        val tokens = TokenFactory().sym("a").punct("=").num("1").punct(";").build()

        val cfg = configStream(
            enforceNoSpacingAroundEquals = true,
            mandatorySingleSpaceSeparation = true,
            spaceBeforeColonFlag = false,
            spaceAfterColonFlag = false
        )

        val (fmt, out) = makeFormatter10(tokens, cfg)
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

        val (fmt, out) = makeFormatter10(tokens, cfg)
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
            spaceBeforeColonFlag = true
        ) // permitir espacio antes de ':'

        val (fmt, out) = makeFormatter10(tokens, cfg)
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
            mandatorySingleSpaceSeparation = true
        ) // activa "every" como relleno

        val (fmt, out) = makeFormatter10(tokens, cfg)
        fmt.getNext()
        assertEquals("1 + 2;", out.toString())
    }

    @Test
    fun `if brace below-line mueve la llave a la linea de abajo`() {
        val tokens = TokenFactory()
            .kw("let").sp().sym("something").punct(":").sp().sym("boolean").sp().punct("=").sp().kw("true").punct(";")
            .kw("if").sp().punct("(").sym("something").punct(")").sp().punct("{").nl().sp(2)
            .printlnLiteral("\"Entered if\"")
            .punct("}")
            .build()

        val cfg = ByteArrayInputStream(
            """
        {
          "if-brace-below-line": true,
          "indent-inside-if": 2
        }
            """.trimIndent().toByteArray()
        )

        val (fmt, out) = makeFormatter11(tokens, cfg) // <-- usa Provider11
        fmt.getNext()

        val expected = """
        let something: boolean = true;
        if (something)
        {
          println("Entered if");
        }
        """.trimIndent()

        assertEquals(expected, out.toString())
    }

    @Test
    fun `if same-line aplica indent=2 en linea siguiente`() {
        val tokens = TokenFactory()
            .kw("if").sp().punct("(").sym("cond").punct(")").sp().punct("{").nl() // salto después de "{"
            .printlnLiteral("\"x\"")
            .punct("}")
            .build()

        val cfg = ByteArrayInputStream(
            """
        {
          "if-brace-same-line": true,
          "if-brace-below-line": false,
          "indent-inside-if": 2
        }
            """.trimIndent().toByteArray()
        )

        val (fmt, out) = makeFormatter11(tokens, cfg)
        fmt.getNext()

        val expected = """
        if (cond) {
          println("x");
        }
        """.trimIndent()
        assertEquals(expected, out.toString())
    }

    @Test
    fun `if below-line baja la llave y aplica indent`() {
        val tokens = TokenFactory()
            .kw("if").sp().punct("(").sym("something").punct(")").sp().punct("{").nl()
            .printlnLiteral("\"Entered if\"")
            .punct("}")
            .build()

        val cfg = ByteArrayInputStream(
            """
        {
          "if-brace-same-line": false,
          "if-brace-below-line": true,
          "indent-inside-if": 2
        }
            """.trimIndent().toByteArray()
        )

        val (fmt, out) = makeFormatter11(tokens, cfg)
        fmt.getNext()

        val expected = """
        if (something)
        {
          println("Entered if");
        }
        """.trimIndent()
        assertEquals(expected, out.toString())
    }

    @Test
    fun `if anidado incrementa y luego dedentea`() {
        val tokens = TokenFactory()
            .kw("if").sp().punct("(").sym("x").punct(")").sp().punct("{").nl()
            .kw("if").sp().punct("(").sym("y").punct(")").sp().punct("{").nl()
            .printlnLiteral("\"two\"")
            .punct("}").nl()
            .punct("}")
            .build()

        val cfg = ByteArrayInputStream(
            """
        {
          "if-brace-same-line": true,
          "if-brace-below-line": false,
          "indent-inside-if": 2
        }
            """.trimIndent().toByteArray()
        )

        val (fmt, out) = makeFormatter11(tokens, cfg)
        fmt.getNext()

        val expected = """
        if (x) {
          if (y) {
            println("two");
          }
        }
        """.trimIndent()
        assertEquals(expected, out.toString())
    }

    @Test
    fun `dedent antes de cierre alinea la llave de cierre`() {
        val tokens = TokenFactory()
            .kw("if").sp().punct("(").sym("k").punct(")").sp().punct("{").nl()
            .printlnLiteral("\"x\"")
            .punct("}")
            .build()

        val cfg = ByteArrayInputStream(
            """
        {
          "if-brace-same-line": true,
          "if-brace-below-line": false,
          "indent-inside-if": 3
        }
            """.trimIndent().toByteArray()
        )

        val (fmt, out) = makeFormatter11(tokens, cfg)
        fmt.getNext()

        val expected = """
        if (k) {
           println("x");
        }
        """.trimIndent()
        assertEquals(expected, out.toString())
    }

    @Test
    fun `if con parentesis anidados mantiene parenDepth y coloca correctamente la llave`() {
        val tokens = TokenFactory()
            .kw("if").sp()
            .punct("(").punct("(").sym("a").sp().op("&").op("&").sp().punct("(").sym("b").sp().op("|").op("|").sp().sym("c").punct(")").punct(")").punct(")").sp()
            .punct("{").nl()
            .printlnLiteral("\"deep\"")
            .punct("}")
            .build()

        val cfg = ByteArrayInputStream(
            """
        {
          "if-brace-same-line": true,
          "if-brace-below-line": false,
          "indent-inside-if": 2
        }
            """.trimIndent().toByteArray()
        )

        val (fmt, out) = makeFormatter11(tokens, cfg)
        fmt.getNext()

        val expected = """
        if ((a && (b || c))) {
          println("deep");
        }
        """.trimIndent()
        assertEquals(expected, out.toString())
    }

    @Test
    fun `below-line prevalece frente a mandatory-single-space`() {
        val tokens = TokenFactory()
            .kw("if").sp().punct("(").sym("p").punct(")").sp().punct("{").nl()
            .printlnLiteral("\"q\"")
            .punct("}")
            .build()

        val cfg = ByteArrayInputStream(
            """
        {
          "if-brace-below-line": true,
          "indent-inside-if": 2,
          "mandatory-single-space-separation": true
        }
            """.trimIndent().toByteArray()
        )

        val (fmt, out) = makeFormatter11(tokens, cfg)
        fmt.getNext()

        val expected = """
        if ( p )
        {
          println ( "q" );
        }
        """.trimIndent()
        assertEquals(expected, out.toString())
    }

    @Test
    fun `no dispara indent si no es if KEYWORD`() {
        val tokens = TokenFactory()
            .sym("iff").sp().punct("(").sym("x").punct(")").sp().punct("{").nl()
            .printlnLiteral("\"no-if\"")
            .punct("}")
            .build()

        val cfg = ByteArrayInputStream(
            """
        {
          "if-brace-same-line": true,
          "if-brace-below-line": false,
          "indent-inside-if": 4
        }
            """.trimIndent().toByteArray()
        )

        val (fmt, out) = makeFormatter11(tokens, cfg)
        fmt.getNext()

        // Como no es KEYWORD "if", la regla de indent no sube nivel; el contenido queda sin sangría
        val expected = """
        iff (x) {
        println("no-if");
        }
        """.trimIndent()
        assertEquals(expected, out.toString())
    }
}
