package handlers

import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.ReadInputExpression
import org.example.ast.expressions.StringExpression
import org.example.common.ErrorHandler
import org.example.common.Position
import org.example.common.Range
import org.example.interpreter.Executor
import org.example.interpreter.asthandlers.ReadInputNodeHandler
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.input.InputProvider
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

    private val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>> = mapOf(
        ReadInputExpression::class.java to ReadInputNodeHandler()
    )

    private val fakeRange = Range(Position(0, 0), Position(0, 0))

    @Test
    fun `should read boolean input correctly`() {
        fakeInputProvider.nextInput = "true"

        val node = ReadInputExpression(
            OptionalExpression.HasExpression(StringExpression("Enter a boolean:", Position(0, 0))),
            fakeRange
        )

        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)
        val result = executor.evaluate(node)

        assertTrue(result is Boolean)
        assertEquals(true, result)
        assertTrue(fakeErrorHandler.errors.isEmpty())
    }

    @Test
    fun `should read string input correctly`() {
        fakeInputProvider.nextInput = "hello"

        val node = ReadInputExpression(
            OptionalExpression.HasExpression(StringExpression("Enter a string:", Position(0, 0))),
            fakeRange
        )

        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)
        val result = executor.evaluate(node)

        assertTrue(result is String)
        assertEquals("hello", result)
        assertTrue(fakeErrorHandler.errors.isEmpty())
    }

    @Test
    fun `should report error if boolean input is invalid`() {
        fakeInputProvider.nextInput = "notABoolean"

        val node = ReadInputExpression(
            OptionalExpression.HasExpression(StringExpression("Enter a boolean:", Position(0, 0))),
            fakeRange
        )

        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)
        executor.evaluate(node)
        val result = executor.popLiteral()

        assertEquals(null, result)
        assertEquals(1, fakeErrorHandler.errors.size)
        assertTrue(fakeErrorHandler.errors[0].contains("Expected BOOLEAN"))
    }

    @Test
    fun `should report error if number input is invalid`() {
        fakeInputProvider.nextInput = "notANumber"

        val node = ReadInputExpression(
            OptionalExpression.HasExpression(StringExpression("Enter a number:", Position(0, 0))),
            fakeRange
        )

        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)
        executor.evaluate(node)
        val result = executor.popLiteral()

        assertEquals(null, result)
        assertEquals(1, fakeErrorHandler.errors.size)
        assertTrue(fakeErrorHandler.errors[0].contains("Expected NUMBER"))
    }
}
