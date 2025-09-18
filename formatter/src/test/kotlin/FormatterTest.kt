import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.common.PrintScriptIterator
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.formatter.Formatter
import org.example.formatter.providers.FormatterProvider10
import org.example.formatter.providers.FormatterVersionProvider
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.StringWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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

        // colon en declaraciones: let x : string
        spaceBeforeColonInDecl: Boolean? = null,
        spaceAfterColonInDecl: Boolean? = null,
        spaceAroundColon: Boolean? = null,

        // equals en declaraciones: let x: string = 1
        spaceBeforeEqualsInDecl: Boolean? = null,
        spaceAfterEqualsInDecl: Boolean? = null,
        spaceAroundEquals: Boolean? = null, // general (asignaciones/otros)

        // println: cantidad de saltos de línea luego del paréntesis
        lineBreaksAfterPrintln: Int? = null
    ): InputStream {
        val parts = mutableListOf<String>()

        if (spacesAroundOperators != null) {
            parts += "\"spacesAroundOperators\": { \"rule\": $spacesAroundOperators }"
        }

        if (spaceBeforeColonInDecl != null) {
            parts += "\"enforce-spacing-before-colon-in-declaration\": { \"rule\": $spaceBeforeColonInDecl }"
        }
        if (spaceAfterColonInDecl != null) {
            parts += "\"enforce-spacing-after-colon-in-declaration\": { \"rule\": $spaceAfterColonInDecl }"
        }
        if (spaceAroundColon != null) {
            parts += "\"enforce-spacing-around-colon\": { \"rule\": $spaceAroundColon }"
        }

        if (spaceBeforeEqualsInDecl != null) {
            parts += "\"enforce-spacing-before-equals-in-declaration\": { \"rule\": $spaceBeforeEqualsInDecl }"
        }
        if (spaceAfterEqualsInDecl != null) {
            parts += "\"enforce-spacing-after-equals-in-declaration\": { \"rule\": $spaceAfterEqualsInDecl }"
        }
        if (spaceAroundEquals != null) {
            parts += "\"enforce-spacing-around-equals\": { \"rule\": $spaceAroundEquals }"
        }

        if (lineBreaksAfterPrintln != null) {
            parts += "\"line-breaks-after-println\": { \"rule\": true, \"quantity\": $lineBreaksAfterPrintln }"
        }

        val json = "{${parts.joinToString(",")}}"
        return ByteArrayInputStream(json.toByteArray(Charsets.UTF_8))
    }

    private fun createFormatter(
        version: String,
        nodes: List<ASTNode>,
        config: InputStream,
        writer: StringWriter = StringWriter()
    ): Pair<Formatter, StringWriter> {
        val provider = FormatterVersionProvider().with(version)
        val formatter = provider.provide(TestIterator(nodes), writer, config)
        return formatter to writer
    }

    // ===== tests =====

    // --- PRINTLN ---

    @Test
    fun `println por defecto - sin saltos y sin punto y coma`() {
        val print = astFactory.createPrintFunction(
            OptionalExpression.HasExpression(astFactory.createString("\"hola\""))
        )

        val (formatter, out) = createFormatter("1.0", listOf(print), configStream())

        assertTrue(formatter.hasNext())
        formatter.getNext()

        assertEquals("println(\"hola\");", out.toString())
    }

    @Test
    fun `println con 2 saltos - solo si hay siguiente nodo`() {
        val p1 = astFactory.createPrintFunction(
            OptionalExpression.HasExpression(astFactory.createString("\"hola\""))
        )

        // caso 1: un solo nodo -> no hay siguiente, NO agrega \n
        run {
            val (formatter, out) = createFormatter(
                "1.0",
                listOf(p1),
                configStream(lineBreaksAfterPrintln = 2)
            )
            while (formatter.hasNext()) formatter.getNext()
            assertEquals("println(\"hola\");", out.toString())
        }

        // caso 2: dos nodos -> el primero agrega 2 \n, el segundo no
        run {
            val p2 = astFactory.createPrintFunction(
                OptionalExpression.HasExpression(astFactory.createString("\"chau\""))
            )
            val (formatter, out) = createFormatter(
                "1.0",
                listOf(p1, p2),
                configStream(lineBreaksAfterPrintln = 2)
            )
            while (formatter.hasNext()) formatter.getNext()
            assertEquals("println(\"hola\");\n\n\nprintln(\"chau\");", out.toString())
        }
    }

    // --- BINARIOS (espacios alrededor de operadores) ---

    @Test
    fun `BinaryExpression con 1 espacio alrededor del operador`() {
        val expr = astFactory.createBinaryExpression(
            astFactory.createNumber("1"),
            Operator.MUL,
            astFactory.createNumber("3")
        )
        val (formatter, out) = createFormatter("1.0", listOf(expr), configStream(spacesAroundOperators = true))

        formatter.getNext()

        assertEquals("1 * 3", out.toString())
        // además validamos que no haya dobles espacios
        assertFalse(out.toString().contains("  "))
    }

    // --- DECLARACIONES (espacios alrededor de ':', '=' en declaraciones) ---

    @Test
    fun `declaracion con spaces around colon y equals - ambos activados`() {
        val decl = astFactory.createVariableDeclarator(
            astFactory.createSymbol("x"),
            Type.STRING,
            OptionalExpression.HasExpression(astFactory.createString("\"a\""))
        )

        val (formatter, out) = createFormatter(
            "1.0",
            listOf(decl),
            configStream(
                spaceAroundColon = true,
                spaceAroundEquals = true
            )
        )

        formatter.getNext()

        assertEquals("let x : string = \"a\";", out.toString())
    }

    @Test
    fun `declaracion con solo espacio antes de '2puntos'`() {
        val decl = astFactory.createVariableDeclarator(
            astFactory.createSymbol("y"),
            Type.NUMBER,
            OptionalExpression.NoExpression
        )

        val (formatter, out) = createFormatter(
            "1.0",
            listOf(decl),
            configStream(
                spaceBeforeColonInDecl = true,
                spaceAfterColonInDecl = false,
                spaceAroundColon = false
            )
        )

        formatter.getNext()
        assertEquals("let y :number;", out.toString())
    }

    @Test
    fun `declaracion con solo espacio despues de '2puntos'`() {
        val decl = astFactory.createVariableDeclarator(
            astFactory.createSymbol("z"),
            Type.BOOLEAN,
            OptionalExpression.NoExpression
        )

        val (formatter, out) = createFormatter(
            "1.0",
            listOf(decl),
            configStream(
                spaceBeforeColonInDecl = false,
                spaceAfterColonInDecl = true,
                spaceAroundColon = false
            )
        )

        formatter.getNext()
        assertEquals("let z: boolean;", out.toString())
    }

    @Test
    fun `declaracion con '=' sin espacios (around=false, before=false, after=false)`() {
        val decl = astFactory.createVariableDeclarator(
            astFactory.createSymbol("a"),
            Type.NUMBER,
            OptionalExpression.HasExpression(astFactory.createNumber("1"))
        )

        val (formatter, out) = createFormatter(
            "1.0",
            listOf(decl),
            configStream(
                spaceAroundColon = true, // para que quede 'a : number'
                spaceAroundEquals = false,
                spaceBeforeEqualsInDecl = false,
                spaceAfterEqualsInDecl = false
            )
        )

        formatter.getNext()
        assertEquals("let a : number=1;", out.toString())
    }

    @Test
    fun `declaracion con '=' solo antes (before=true)`() {
        val decl = astFactory.createVariableDeclarator(
            astFactory.createSymbol("b"),
            Type.STRING,
            OptionalExpression.HasExpression(astFactory.createString("\"x\""))
        )

        val (formatter, out) = createFormatter(
            "1.0",
            listOf(decl),
            configStream(
                spaceAroundColon = false, // sin espacios alrededor del ':'
                spaceBeforeEqualsInDecl = true,
                spaceAfterEqualsInDecl = false,
                spaceAroundEquals = false
            )
        )

        formatter.getNext()
        assertEquals("let b:string =\"x\";", out.toString())
    }

    @Test
    fun `declaracion con '=' solo despues (after=true)`() {
        val decl = astFactory.createVariableDeclarator(
            astFactory.createSymbol("c"),
            Type.NUMBER,
            OptionalExpression.HasExpression(astFactory.createNumber("10"))
        )

        val (formatter, out) = createFormatter(
            "1.0",
            listOf(decl),
            configStream(
                spaceAroundColon = false,
                spaceBeforeEqualsInDecl = false,
                spaceAfterEqualsInDecl = true,
                spaceAroundEquals = false
            )
        )

        formatter.getNext()
        assertEquals("let c:number= 10;", out.toString())
    }
    // --- ASIGNACIONES (usa 'enforce-spacing-around-equals') ---

    @Test
    fun `assignment con espacios alrededor de '=' (around=true)`() {
        val assign: VariableAssigner = astFactory.createVariableAssigment(
            astFactory.createSymbol("userName"),
            OptionalExpression.HasExpression(astFactory.createNumber("1"))
        )

        val (formatter, out) = createFormatter(
            "1.0",
            listOf(assign),
            configStream(spaceAroundEquals = true)
        )

        formatter.getNext()
        assertEquals("userName = 1;", out.toString())
    }

    @Test
    fun `assignment sin espacios alrededor de '=' (around=false)`() {
        val assign: VariableAssigner = astFactory.createVariableAssigment(
            astFactory.createSymbol("userName"),
            OptionalExpression.HasExpression(astFactory.createNumber("1"))
        )

        val (formatter, out) = createFormatter(
            "1.0",
            listOf(assign),
            configStream(spaceAroundEquals = false)
        )

        formatter.getNext()
        assertEquals("userName=1;", out.toString())
    }

    // --- MULTI-NODOS + reglas mixtas y chequeo de 1 solo espacio entre tokens ---

    @Test
    fun `pipeline - declara con colon around y equals around, binario con espacios, y asignacion sin espacios`() {
        val decl = astFactory.createVariableDeclarator(
            astFactory.createSymbol("answer"),
            Type.NUMBER,
            OptionalExpression.HasExpression(astFactory.createNumber("42"))
        )
        val assign = astFactory.createVariableAssigment(
            astFactory.createSymbol("x"),
            OptionalExpression.HasExpression(astFactory.createNumber("3"))
        )

        val nodes = listOf(decl, assign)

        val (formatter, out) = createFormatter(
            "1.0",
            nodes,
            configStream(
                spaceAroundColon = true,
                spaceAroundEquals = false
            )
        )

        while (formatter.hasNext()) formatter.getNext()

        val expected = buildString {
            append("let answer : number=42;\n")
            append("x=3;")
        }
        assertEquals(expected, out.toString())
        assertFalse(out.toString().contains("  "), "No debe haber dobles espacios")
    }


    @Test
    fun `immutable declarators con around en colon y equals, y boolean+symbol como valores`() {
        // const status : boolean = true;
        val constBool: VariableImmutableDeclarator = astFactory.createVariableImmutableDeclarator(
            astFactory.createSymbol("status"),
            Type.BOOLEAN,
            OptionalExpression.HasExpression(astFactory.createBoolean("true"))
        )
        // const alias : string = userName;
        val constSym: VariableImmutableDeclarator = astFactory.createVariableImmutableDeclarator(
            astFactory.createSymbol("alias"),
            Type.STRING,
            OptionalExpression.HasExpression(astFactory.createSymbol("userName"))
        )
        // nodo final: BooleanExpression suelto para tocar BooleanExpressionFormat también
        val boolLoose = astFactory.createBoolean("false")

        val nodes = listOf(constBool, constSym, boolLoose)

        val (fmt, out) = createFormatter(
            "1.1",
            nodes,
            configStream(
                spaceAroundColon = true,
                spaceAroundEquals = true
            )
        )

        while (fmt.hasNext()) fmt.getNext()

        // Regla en VariableInmutableDeclaratorFormat: escribe ';' y agrega '\n' solo si hay siguiente.
        // constBool -> tiene siguiente -> '\n'
        // constSym  -> tiene siguiente -> '\n'
        // boolLoose -> no agrega ';' ni '\n'
        val expected = buildString {
            append("const status : boolean = true;\n")
            append("const alias : string = userName;\n")
            append("false")
        }
        assertEquals(expected, out.toString())
    }

    @Test
    fun `immutable declarator sin valor + combinaciones de espacios en colon y equals`() {
        // const k :number;   (solo espacio antes de ':', sin valor)
        val constNoValue: VariableImmutableDeclarator = astFactory.createVariableImmutableDeclarator(
            astFactory.createSymbol("k"),
            Type.NUMBER,
            OptionalExpression.NoExpression
        )
        // const v:string =10;  (sin around en ':', '=' con espacio solo antes)
        val constNumValue: VariableImmutableDeclarator = astFactory.createVariableImmutableDeclarator(
            astFactory.createSymbol("v"),
            Type.STRING,
            OptionalExpression.HasExpression(astFactory.createNumber("10"))
        )
        // Agregamos un boolean al final para que el segundo const también agregue '\n'
        val tail = astFactory.createBoolean("true")

        val nodes = listOf(constNoValue, constNumValue, tail)

        val (fmt, out) = createFormatter(
            "1.1",
            nodes,
            configStream(
                // para k: espacio antes de ':' solamente
                spaceBeforeColonInDecl = true,
                spaceAfterColonInDecl = false,
                spaceAroundColon = false,
                // para v: '=' con espacio solo antes, y sin around
                spaceBeforeEqualsInDecl = true,
                spaceAfterEqualsInDecl = false,
                spaceAroundEquals = false
            )
        )

        while (fmt.hasNext()) fmt.getNext()

        // k -> "const k :number;" + '\n' (tiene siguiente)
        // v -> "const v:string =10;" + '\n' (tiene siguiente)
        // tail -> "true"
        val expected = buildString {
            append("const k :number;\n")
            append("const v :string =10;\n")
            append("true")
        }
        assertEquals(expected, out.toString())
    }

    @Test
    fun `readEnv con string literal - no cierra parentesis ni pone 'semicolon'`() {
        // readEnv("HOME")
        val readEnv = astFactory.createReadEnv(
            OptionalExpression.HasExpression(astFactory.createString("\"HOME\""))
        )

        val (fmt, out) = createFormatter("1.1", listOf(readEnv), configStream())
        assertTrue(fmt.hasNext())
        fmt.getNext()

        assertEquals("readEnv(\"HOME\")", out.toString())
    }

    @Test
    fun `readInput con simbolo - no cierra parentesis ni pone 'semicolon'`() {
        // readInput(userName)
        val readInput = astFactory.createReadInput(
            OptionalExpression.HasExpression(astFactory.createSymbol("userName"))
        )

        val (fmt, out) = createFormatter("1.1", listOf(readInput), configStream())
        assertTrue(fmt.hasNext())
        fmt.getNext()

        assertEquals("readInput(userName)", out.toString())
    }

    @Test
    fun `readInput con binaria - spacesAroundOperators=false`() {
        // readInput(1+2)
        val bin = astFactory.createBinaryExpression(
            astFactory.createNumber("1"), Operator.ADD, astFactory.createNumber("2")
        )
        val readInput = astFactory.createReadInput(OptionalExpression.HasExpression(bin))

        val (fmt, out) = createFormatter(
            "1.1",
            listOf(readInput),
            configStream(spacesAroundOperators = false)
        )
        assertTrue(fmt.hasNext())
        fmt.getNext()

        assertEquals("readInput(1 + 2)", out.toString())
    }

    @Test
    fun `readInput con binaria - spacesAroundOperators=true`() {
        // readInput(1 + 2)
        val bin = astFactory.createBinaryExpression(
            astFactory.createNumber("1"), Operator.ADD, astFactory.createNumber("2")
        )
        val readInput = astFactory.createReadInput(OptionalExpression.HasExpression(bin))

        val (fmt, out) = createFormatter(
            "1.1",
            listOf(readInput),
            configStream(spacesAroundOperators = true)
        )
        assertTrue(fmt.hasNext())
        fmt.getNext()

        assertEquals("readInput(1 + 2)", out.toString())
    }
}
