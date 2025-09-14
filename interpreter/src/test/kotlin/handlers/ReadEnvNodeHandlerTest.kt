package handlers

import org.example.ast.ASTNode
import org.example.ast.expressions.ReadEnvNode
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.ast_handlers.ReadEnvNodeHandler
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.output.ErrorHandler
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

    val fakeInputProvider = object : org.example.interpreter.input.InputProvider {
        override fun readInput(prompt: String): String = ""
    }


    private val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>> = mapOf(
        ReadEnvNode::class.java to ReadEnvNodeHandler()
    )

    @Test
    fun `should read string env variable correctly`() {
        val varName = "PATH"
        val node = ReadEnvNode(varName, Type.STRING)
        val executor = Executor(
            handlers,
            inputProvider = fakeInputProvider,
            outputPrinter = fakePrinter,
            errorHandler = fakeErrorHandler
        )

        val result = executor.evaluate(node)

        assertTrue(result is String)
        assertTrue((result as String).isNotEmpty())
        assertTrue(fakeErrorHandler.errors.isEmpty())
    }


    @Test
    fun `should report error if env variable not found`() {
        val node = ReadEnvNode("NON_EXISTENT_VAR", Type.STRING)
        val fakeInputProvider = object : org.example.interpreter.input.InputProvider {
            override fun readInput(prompt: String): String = ""
        }
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

    private fun setEnv(vararg pairs: Pair<String, String>) {
        val env = System.getenv() as MutableMap
        pairs.forEach { (k, v) -> env[k] = v }
    }



}
