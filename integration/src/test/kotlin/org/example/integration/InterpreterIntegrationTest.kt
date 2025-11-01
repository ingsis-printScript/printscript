package org.example.integration

import org.example.common.ErrorHandler
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.OutputPrinter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests de integración full pipeline:
 * Lexer → Parser → Interpreter → Output
 */
class InterpreterIntegrationTest {

    private lateinit var interpreter: Interpreter
    private lateinit var printer: FakeOutputPrinter
    private lateinit var handler: FakeErrorHandler
    private lateinit var input: FakeInputProvider

    @BeforeEach
    fun setup() {
        interpreter = Interpreter()
        printer = FakeOutputPrinter()
        handler = FakeErrorHandler()
        input = FakeInputProvider()
    }

    private fun run(code: String, version: String = "1.0") {
        interpreter.execute(
            src = code.lineSequence().iterator(),
            version = version,
            emitter = printer,
            handler = handler,
            provider = input
        )
    }

    @Test
    fun `suma simple imprime resultado`() {
        run(
            """
            let a: number = 10;
            let b: number = 20;
            println(a + b);
            """.trimIndent()
        )

        assertEquals(listOf("30"), printer.outputs)
        assertEquals(emptyList<String>(), handler.errors)
    }

    @Test
    fun `concatenacion string y number`() {
        run(
            """
            let name: string = "Alice ";
            let age: number = 30;
            println(name + age);
            """.trimIndent()
        )

        assertEquals(listOf("Alice 30"), printer.outputs)
    }

    @Test
    fun `reasignaciones sucesivas`() {
        run(
            """
            let counter: number = 1;
            counter = counter + 1;
            counter = counter + 5;
            println(counter);
            """.trimIndent()
        )

        assertEquals(listOf("7"), printer.outputs)
    }

    @Test
    fun `uso de variable no declarada produce error`() {
        run("x = 5;")

        assertEquals(emptyList<String>(), printer.outputs)
        assertEquals(1, handler.errors.size)
        assert(handler.errors.first().contains("not declared"))
    }

    @Test
    fun `reasignacion ilegal simulada produce error`() {
        run(
            """
        let x = 1;
        x = 2;
        """.trimIndent()
        )

        assertEquals(emptyList<String>(), printer.outputs)
        assert(handler.errors.isNotEmpty())
    }

    @Test
    fun `operaciones aritmeticas con decimales`() {
        run("""
        let pi: number = 3.14;
        println(pi / 2);
    """.trimIndent())

        assertEquals(listOf("1.57"), printer.outputs) // depende de cómo tu intérprete redondee
    }

    @Test
    fun `concatenacion de multiples strings`() {
        run("""
        let hello: string = "Hello";
        let world: string = "World";
        println(hello + " " + world + "!");
    """.trimIndent())

        assertEquals(listOf("Hello World!"), printer.outputs)
    }


    @Test
    fun `operaciones de asignacion compuesta`() {
        run("""
        let a: number = 5;
        a = a + 10;
        println(a);
    """.trimIndent())

        assertEquals(listOf("15"), printer.outputs)
    }

    @Test
    fun `string vacio y concatenacion`() {
        run("""
        let prefix: string = "";
        println(prefix + "hello");
    """.trimIndent())

        assertEquals(listOf("hello"), printer.outputs)
    }

    @Test
    fun `error de tipo al sumar string y number sin concatenacion`() {
        run("""
        let a: number = 5;
        let b: string = "test";
        let c: number = a + b;
    """.trimIndent())

        assertEquals(emptyList<String>(), printer.outputs)
        assert(handler.errors.isNotEmpty())
    }


}

/**
 * Implementaciones fake para capturar output y errores
 */
class FakeOutputPrinter : OutputPrinter {
    val outputs = mutableListOf<String>()
    override fun print(output: String) {
        outputs.add(output)
    }
}

class FakeErrorHandler : ErrorHandler {
    val errors = mutableListOf<String>()
    override fun handleError(error: String) {
        errors.add(error)
    }
}
class FakeInputProvider : InputProvider {
    private val inputs = mutableListOf<String>()
    fun enqueue(value: String) = inputs.add(value)

    override fun readInput(): String {
        return if (inputs.isNotEmpty()) inputs.removeAt(0) else ""
    }
}
