import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.functions.PrintFunction
import org.example.common.PrintScriptIterator
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.formatter.Formatter
import org.example.formatter.providers.FormatterProvider10
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.StringWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormatterTest {

    class TestIterator(private val items: List<ASTNode>) : PrintScriptIterator<Result> {
        private var index = 0
        override fun hasNext() = index < items.size
        override fun getNext(): Result = Success(items[index++])
    }

    private val astFactory = AstFactory()

    private fun configStream(
        spacesAroundOperators: Boolean? = null,
        indentationQty: Int? = null
    ): InputStream {
        val parts = mutableListOf<String>()
        if (spacesAroundOperators != null) {
            parts += "\"spacesAroundOperators\": { \"rule\": $spacesAroundOperators }"
        }
        if (indentationQty != null) {
            parts += "\"indentation\": { \"rule\": true, \"quantity\": $indentationQty }"
        }
        val json = "{${parts.joinToString(",")}}"
        return ByteArrayInputStream(json.toByteArray(Charsets.UTF_8))
    }

    private fun createFormatter(
        nodes: List<ASTNode>,
        config: InputStream,
        writer: StringWriter = StringWriter()
    ): Pair<Formatter, StringWriter> {
        val provider = FormatterProvider10()
        val formatter = provider.provide(TestIterator(nodes), writer, config)
        return formatter to writer
    }

    // ===== tests =====

    @Test
    fun `println con string literal - reglas por defecto`() {
        val print: PrintFunction = astFactory.createPrintFunction(
            OptionalExpression.HasExpression(astFactory.createString("\"hola\""))
        )

        val (formatter, out) = createFormatter(listOf(print), configStream())

        assertTrue(formatter.hasNext())
        formatter.getNext()

        assertEquals("println(\"hola\");\n", out.toString())
    }

    @Test
    fun `BinaryExpression con spacesAroundOperators=false`() {
        val expr = astFactory.createBinaryExpression(
            astFactory.createNumber("1"),
            Operator.ADD,
            astFactory.createNumber("2")
        )

        val (formatter, out) = createFormatter(
            listOf(expr),
            configStream(spacesAroundOperators = false)
        )

        assertTrue(formatter.hasNext())
        formatter.getNext()

        assertEquals("1+2;\n", out.toString())
    }

    @Test
    fun `BinaryExpression con spacesAroundOperators=true (explícito)`() {
        val expr = astFactory.createBinaryExpression(
            astFactory.createNumber("1"),
            Operator.ADD,
            astFactory.createNumber("2")
        )

        val (formatter, out) = createFormatter(
            listOf(expr),
            configStream(spacesAroundOperators = true)
        )

        assertTrue(formatter.hasNext())
        formatter.getNext()

        assertEquals("1 + 2;\n", out.toString())
    }

    @Test
    fun `VariableDeclarator con asignación string`() {
        val decl = astFactory.createVariableDeclarator(
            astFactory.createSymbol("userName"),
            Type.STRING,
            OptionalExpression.HasExpression(astFactory.createString("\"Juan\""))
        )

        val (formatter, out) = createFormatter(listOf(decl), configStream())

        assertTrue(formatter.hasNext())
        formatter.getNext()

        assertEquals("let userName: string = \"Juan\";\n", out.toString())
    }

    @Test
    fun `VariableAssigner simple - respeta espacios alrededor del '=' (si aplica)`() {
        val assign: VariableAssigner = astFactory.createVariableAssigment(
            astFactory.createSymbol("userName"),
            OptionalExpression.HasExpression(astFactory.createNumber("1"))
        )

        val (formatter, out) = createFormatter(listOf(assign), configStream())

        assertTrue(formatter.hasNext())
        formatter.getNext()

        // Si tu VariableAssignerFormat no agrega espacios, cambiá a "userName=1;\n"
        assertEquals("userName = 1;\n", out.toString())
    }

    @Test
    fun `múltiples nodos - preserva orden y aplica mismas reglas`() {
        val decl = astFactory.createVariableDeclarator(
            astFactory.createSymbol("a"),
            Type.NUMBER,
            OptionalExpression.HasExpression(astFactory.createNumber("1"))
        )
        val expr = astFactory.createBinaryExpression(
            astFactory.createNumber("1"),
            Operator.ADD,
            astFactory.createNumber("2")
        )
        val print = astFactory.createPrintFunction(
            OptionalExpression.HasExpression(expr)
        )

        val nodes = listOf(decl, expr, print)
        val (formatter, out) = createFormatter(
            nodes,
            configStream(spacesAroundOperators = true)
        )

        while (formatter.hasNext()) formatter.getNext()

        val expected = listOf(
            "let a: number = 1;",
            "1 + 2;",
            "println(1 + 2);"
        ).joinToString(separator = "\n", postfix = "\n")

        assertEquals(expected, out.toString())
    }

    @Test
    fun `SymbolExpression aislado (línea simple)`() {
        val sym = astFactory.createSymbol("x")

        val (formatter, out) = createFormatter(listOf(sym), configStream())

        assertTrue(formatter.hasNext())
        formatter.getNext()

        // Ajustá si tu SymbolExpressionFormat imprime distinto
        assertEquals("x;\n", out.toString())
    }
}