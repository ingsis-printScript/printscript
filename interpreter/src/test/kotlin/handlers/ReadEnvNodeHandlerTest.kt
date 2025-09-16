package handlers

import org.example.ast.ASTNode
import org.example.ast.expressions.ReadEnvExpression
import org.example.common.ErrorHandler
import org.example.common.Position
import org.example.common.Range
import org.example.interpreter.Executor
import org.example.interpreter.asthandlers.ReadEnvNodeHandler
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.OutputPrinter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReadEnvNodeHandlerTest {

    private val fakeErrorHandler = object : ErrorHandler {
        val errors = mutableListOf<String>()
        override fun handleError(message: String) { errors.add(message) }
    }

    private val fakePrinter = object : OutputPrinter {
        val printed = mutableListOf<String>()
        override fun print(value: String) { printed.add(value) }
    }

    private val fakeInputProvider = object : InputProvider {
        override fun readInput(prompt: String): String = ""
    }

    private val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>> = mapOf(
        ReadEnvExpression::class.java to ReadEnvNodeHandler()
    )

    @Test
    fun `should read string env variable correctly`() {
        val startPos = Position(0, 0)
        val endPos = Position(0, 0)
        val range = Range(startPos, endPos)
        val node = ReadEnvExpression("PATH", range)
        val executor = Executor(
            handlers,
            inputProvider = fakeInputProvider,
            outputPrinter = fakePrinter,
            errorHandler = fakeErrorHandler
        )

        executor.declareVariable("PATH", "/usr/bin")

        val result = executor.evaluate(node)

        assertTrue(result is String)
        assertEquals("/usr/bin", result)
        assertTrue(fakeErrorHandler.errors.isEmpty())
    }

    @Test
    fun `should report error if env variable not found`() {
        val startPos = Position(0, 0)
        val endPos = Position(0, 0)
        val range = Range(startPos, endPos)
        val node = ReadEnvExpression("NON_EXISTENT_VAR", range)
        val executor = Executor(
            handlers,
            inputProvider = fakeInputProvider,
            outputPrinter = fakePrinter,
            errorHandler = fakeErrorHandler
        )

        val result = executor.evaluate(node)

        assertEquals(null, result)
        assertTrue(fakeErrorHandler.errors.isNotEmpty())
    }
}
