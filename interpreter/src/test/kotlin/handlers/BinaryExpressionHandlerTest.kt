import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.NumberExpression
import org.example.common.Position
import org.example.common.PrintScriptIterator
import org.example.common.Range
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Interpreter
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.handlers.BinaryExpressionHandler
import org.example.interpreter.handlers.NumberExpressionHandler
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.ErrorHandler
import org.example.interpreter.output.OutputPrinter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class BinaryExpressionHandlerTest {

    private val dummyPos = Position(0, 0)
    private val dummyRange = Range(dummyPos, dummyPos)

    // Fakes mínimos
    private val fakeInputProvider = object : InputProvider {
        override fun readInput(prompt: String) = ""
    }
    private val fakePrinter = object : OutputPrinter {
        val printed = mutableListOf<String>()
        override fun print(value: String) { printed.add(value) }
    }
    private val fakeErrorHandler = object : ErrorHandler {
        val errors = mutableListOf<String>()
        override fun handleError(message: String) { errors.add(message) }
    }

    private val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>> = mapOf(
        NumberExpression::class.java to NumberExpressionHandler() as ASTNodeHandler<*>,
        BinaryExpression::class.java to BinaryExpressionHandler() as ASTNodeHandler<*>
    )

    @Test
    fun `should evaluate addition of two numbers`() {
        val expr = BinaryExpression(
            left = NumberExpression("5", dummyPos),
            operator = Operator.ADD,
            right = NumberExpression("3", dummyPos),
            range = dummyRange
        )

        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)
        val validator = Validator(handlers, fakeErrorHandler)

        val astNodes: List<ASTNode> = listOf(expr)
        val iterator = object : PrintScriptIterator<ASTNode> {
            var index = 0
            override fun hasNext() = index < astNodes.size
            override fun getNext() = astNodes[index++]
        }

        val interpreter = Interpreter(iterator, validator, executor)

        interpreter.run()

        val result = executor.popLiteral() as Int

        assertEquals(8, result)
        assertTrue(fakeErrorHandler.errors.isEmpty())
    }

    @Test
    fun `should validate addition of two numbers`() {
        val expr = BinaryExpression(
            left = NumberExpression("5", dummyPos),
            operator = Operator.ADD,
            right = NumberExpression("3", dummyPos),
            range = dummyRange
        )

        val validator = Validator(handlers, fakeErrorHandler)
        val resultType = validator.evaluate(expr)

        assertEquals(Type.NUMBER, resultType)
        assertTrue(fakeErrorHandler.errors.isEmpty())
    }
}

