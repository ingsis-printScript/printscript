import org.example.ast.ASTNode
import org.example.ast.expressions.ReadInputNode
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.ast_handlers.ReadInputNodeHandler
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.ErrorHandler
import org.example.interpreter.output.OutputPrinter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReadInputNodeHandlerTest {

    private val fakeInputProvider = object : InputProvider {
        var nextInput = "true"
        override fun readInput(prompt: String) = nextInput
    }

    private val fakeErrorHandler = object : ErrorHandler {
        val errors = mutableListOf<String>()
        override fun handleError(message: String) { errors.add(message) }
    }

    private val fakePrinter = object : OutputPrinter {
        val printed = mutableListOf<String>()
        override fun print(value: String) { printed.add(value) }
    }

    // <-- handlers con el tipo correcto
    private val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>> = mapOf(
        ReadInputNode::class.java to ReadInputNodeHandler()
    )

    @Test
    fun `should read boolean input correctly`() {
        fakeInputProvider.nextInput = "true"

        val node = ReadInputNode("Enter a boolean:", Type.BOOLEAN)
        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)

        val result = executor.evaluate(node)
        assertTrue(result is Boolean)
        assertEquals(true, result)
        assertTrue(fakeErrorHandler.errors.isEmpty())
    }


    @Test
    fun `should report error if boolean input is invalid`() {
        fakeInputProvider.nextInput = "notABoolean"

        val node = ReadInputNode("Enter a boolean:", Type.BOOLEAN)
        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)

        executor.evaluate(node)
        val result = executor.popLiteral()

        // Como hubo error, result debe ser null
        assertEquals(null, result)
        assertEquals(1, fakeErrorHandler.errors.size)
        assertTrue(fakeErrorHandler.errors[0].contains("Expected BOOLEAN"))
    }


    @Test
    fun `should report error if number input is invalid`() {
        fakeInputProvider.nextInput = "notANumber"

        val node = ReadInputNode("Enter a number:", Type.NUMBER)
        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)

        executor.evaluate(node)
        val result = executor.popLiteral()

        assertEquals(null, result)
        assertEquals(1, fakeErrorHandler.errors.size)
        assertTrue(fakeErrorHandler.errors[0].contains("Expected NUMBER"))
    }



    @Test
    fun `should read string input correctly`() {
        fakeInputProvider.nextInput = "hello"

        val node = ReadInputNode("Enter a string:", Type.STRING)
        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)

        val result = executor.evaluate(node)

        assertTrue(result is String)
        assertEquals("hello", result)
        assertTrue(fakeErrorHandler.errors.isEmpty())
    }


}
