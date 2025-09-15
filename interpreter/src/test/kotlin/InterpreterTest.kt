import org.example.ast.ASTNode
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.Statement
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.interpreter.output.ErrorHandler
import org.example.common.Position
import org.example.common.PrintScriptIterator
import org.example.common.Range
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Interpreter
import org.example.interpreter.Validator
import org.example.interpreter.asthandlers.NumberExpressionHandler
import org.example.interpreter.asthandlers.PrintFunctionHandler
import org.example.interpreter.asthandlers.SymbolExpressionHandler
import org.example.interpreter.asthandlers.VariableDeclaratorHandler
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.OutputPrinter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InterpreterTest {

    class TestIterator<ASTNode>(private val items: List<ASTNode>) : PrintScriptIterator<ASTNode> {
        private var index = 0
        override fun hasNext() = index < items.size
        override fun getNext(): ASTNode = items[index++]
    }

    @Test
    fun `run should execute all AST nodes`() {
        // --- SETUP ---
        val fakePrinter = object : OutputPrinter {
            val printed = mutableListOf<String>()
            override fun print(value: String) {
                printed.add(value)
            }
        }

        val fakeErrorHandler = object : ErrorHandler {
            val errors = mutableListOf<String>()
            override fun handleError(message: String) {
                errors.add(message)
            }
        }

        val fakeInputProvider = object : InputProvider {
            override fun readInput(prompt: String) = "123"
        }

        val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>> = mapOf(
            NumberExpression::class.java to NumberExpressionHandler(),
            PrintFunction::class.java to PrintFunctionHandler()
        )

        val dummyPosition = Position(0, 0)
        val dummyRange = Range(Position(0, 0), Position(0, 0))

        val astNodes: List<ASTNode> = listOf(
            PrintFunction(OptionalExpression.HasExpression(NumberExpression("10", dummyPosition)), dummyRange),
            PrintFunction(OptionalExpression.HasExpression(NumberExpression("20", dummyPosition)), dummyRange)
        )

        val iterator = TestIterator(astNodes)
        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)
        val validator = Validator(handlers, fakeErrorHandler)
        val interpreter = Interpreter(iterator, validator, executor)

        // --- ACT ---
        val results = interpreter.run()

        // --- ASSERT ---
        assertEquals(listOf("10.0", "20.0"), fakePrinter.printed)
        assertTrue(fakeErrorHandler.errors.isEmpty())
        assertEquals(2, results.size)
    }

    @Test
    fun `should declare variable and print its value`() {
        // --- SETUP ---
        val fakePrinter = object : OutputPrinter {
            val printed = mutableListOf<String>()
            override fun print(value: String) {
                printed.add(value)
            }
        }
        val fakeErrorHandler = object : ErrorHandler {
            val errors = mutableListOf<String>()
            override fun handleError(message: String) { errors.add(message) }
        }
        val fakeInputProvider = object : InputProvider {
            override fun readInput(prompt: String) = "ignored"
        }

        val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>> = getHandlers()

        val dummyPos = Position(0, 0)
        val dummyRange = Range(dummyPos, dummyPos)

        val astNodes: List<ASTNode> = getAstNodes(dummyPos, dummyRange)

        val iterator = TestIterator(astNodes)
        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)
        val validator = Validator(handlers, fakeErrorHandler)
        val interpreter = Interpreter(iterator, validator, executor)

        // --- ACT ---
        interpreter.run()

        // --- ASSERT ---
        assertEquals(listOf("42.0"), fakePrinter.printed)
        assertTrue(fakeErrorHandler.errors.isEmpty())
    }

    private fun getHandlers(): Map<Class<out ASTNode>, ASTNodeHandler<out ASTNode>> = mapOf(
        NumberExpression::class.java to NumberExpressionHandler(),
        PrintFunction::class.java to PrintFunctionHandler(),
        VariableDeclarator::class.java to VariableDeclaratorHandler(),
        SymbolExpression::class.java to SymbolExpressionHandler()
    )

    private fun getAstNodes(
        dummyPos: Position,
        dummyRange: Range
    ): List<Statement> = listOf(
        VariableDeclarator(
            SymbolExpression("x", dummyPos),
            Type.NUMBER,
            dummyRange,
            OptionalExpression.HasExpression(NumberExpression("42", dummyPos))
        ),
        PrintFunction(
            OptionalExpression.HasExpression(SymbolExpression("x", dummyPos)),
            dummyRange
        )
    )

    @Test
    fun `should report error when using undefined symbol`() {
        // --- SETUP ---
        val fakePrinter = object : OutputPrinter {
            val printed = mutableListOf<String>()
            override fun print(value: String) { printed.add(value) }
        }
        val fakeErrorHandler = object : ErrorHandler {
            val errors = mutableListOf<String>()
            override fun handleError(message: String) { errors.add(message) }
        }
        val fakeInputProvider = object : InputProvider {
            override fun readInput(prompt: String) = "ignored"
        }

        val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>> = mapOf(
            PrintFunction::class.java to PrintFunctionHandler(),
            org.example.ast.expressions.SymbolExpression::class.java to SymbolExpressionHandler()
        )

        val dummyPos = Position(0, 0)
        val dummyRange = Range(dummyPos, dummyPos)

        val astNodes: List<ASTNode> = listOf(
            PrintFunction(
                OptionalExpression.HasExpression(org.example.ast.expressions.SymbolExpression("y", dummyPos)),
                dummyRange
            )
        )

        val iterator = InterpreterTest.TestIterator(astNodes)
        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)
        val validator = Validator(handlers, fakeErrorHandler)
        val interpreter = Interpreter(iterator, validator, executor)

        // --- ACT ---
        interpreter.run()

        // --- ASSERT ---
        assertTrue(fakePrinter.printed.isEmpty())
        assertEquals(1, fakeErrorHandler.errors.size)
        assertTrue(fakeErrorHandler.errors.first().contains("Undefined symbol"))
    }
}
