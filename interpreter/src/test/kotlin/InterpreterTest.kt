import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.Statement
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.Position
import org.example.common.PrintScriptIterator
import org.example.common.Range
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.interpreter.Executor
import org.example.interpreter.Interpreter
import org.example.interpreter.Validator
import org.example.interpreter.asthandlers.BinaryExpressionHandler
import org.example.interpreter.asthandlers.NumberExpressionHandler
import org.example.interpreter.asthandlers.PrintFunctionHandler
import org.example.interpreter.asthandlers.SymbolExpressionHandler
import org.example.interpreter.asthandlers.VariableDeclaratorHandler
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.ErrorHandler
import org.example.interpreter.output.OutputPrinter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InterpreterTest {

    class TestIterator(private val items: List<ASTNode>) : PrintScriptIterator<Result> {
        private var index = 0
        override fun hasNext() = index < items.size
        override fun getNext(): Result = Success(items[index++])
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

        val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>> = getHandlers1()

        val dummyPosition = Position(0, 0)
        val dummyRange = Range(Position(0, 0), Position(0, 0))

        val astNodes: List<ASTNode> = getAstNodes1(dummyPosition, dummyRange)

        val iterator = TestIterator(astNodes)
        val executor = Executor(handlers, fakeInputProvider, fakePrinter, fakeErrorHandler)
        val validator = Validator(handlers, fakeErrorHandler)
        val interpreter = Interpreter(iterator, validator, executor)

        // --- ACT ---
        val results = interpreter.run()

        // --- ASSERT ---
        assertEquals(listOf("10.2", "20", "50"), fakePrinter.printed)
        assertTrue(fakeErrorHandler.errors.isEmpty())
        assertEquals(3, results.size)
    }

    private fun getHandlers1() = mapOf(
        NumberExpression::class.java to NumberExpressionHandler(),
        BinaryExpression::class.java to BinaryExpressionHandler(),
        PrintFunction::class.java to PrintFunctionHandler()
    )

    private fun getAstNodes1(
        dummyPosition: Position,
        dummyRange: Range
    ) = listOf(
        PrintFunction(OptionalExpression.HasExpression(NumberExpression("10.2", dummyPosition)), dummyRange),
        PrintFunction(OptionalExpression.HasExpression(NumberExpression("20", dummyPosition)), dummyRange),
        PrintFunction(
            OptionalExpression.HasExpression(
                BinaryExpression(
                    NumberExpression("30", dummyPosition),
                    Operator.ADD,
                    NumberExpression("20", dummyPosition),
                    dummyRange
                )
            ),
            dummyRange
        )
    )

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
        assertEquals(listOf("42"), fakePrinter.printed)
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

        val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>> = getHandlers2()

        val dummyPos = Position(0, 0)
        val dummyRange = Range(dummyPos, dummyPos)

        val astNodes: List<ASTNode> = getAstNodes2(dummyPos, dummyRange)

        val iterator = TestIterator(astNodes)
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

    private fun getAstNodes2(
        dummyPos: Position,
        dummyRange: Range
    ) = listOf(
        PrintFunction(
            OptionalExpression.HasExpression(SymbolExpression("y", dummyPos)),
            dummyRange
        )
    )

    private fun getHandlers2() = mapOf(
        PrintFunction::class.java to PrintFunctionHandler(),
        SymbolExpression::class.java to SymbolExpressionHandler()
    )
}
