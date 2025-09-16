import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.Statement
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.ErrorHandler
import org.example.common.Position
import org.example.common.Range
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.interpreter.Executor
import org.example.interpreter.Interpreter
import org.example.interpreter.Validator
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.OutputPrinter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InterpreterTest {

    class TestIterator(private val items: List<ASTNode>) : org.example.common.PrintScriptIterator<Result> {
        private var index = 0
        override fun hasNext() = index < items.size
        override fun getNext(): Result = Success(items[index++])
    }

    @Test
    fun `run should execute all AST nodes`() {
        val printed = mutableListOf<String>()
        val errors = mutableListOf<String>()

        val fakePrinter = object : OutputPrinter {
            override fun print(value: String) { printed.add(value) }
        }

        val fakeErrorHandler = object : ErrorHandler {
            override fun handleError(message: String) { errors.add(message) }
        }

        val fakeInputProvider = object : InputProvider {
            override fun readInput(prompt: String) = "123"
        }

        val dummyPos = Position(0, 0)
        val dummyRange = Range(dummyPos, dummyPos)

        val astNodes: List<ASTNode> = listOf(
            PrintFunction(OptionalExpression.HasExpression(NumberExpression("10.2", dummyPos)), dummyRange),
            PrintFunction(OptionalExpression.HasExpression(NumberExpression("20", dummyPos)), dummyRange),
            PrintFunction(
                OptionalExpression.HasExpression(
                    BinaryExpression(
                        NumberExpression("30", dummyPos),
                        org.example.common.enums.Operator.ADD,
                        NumberExpression("20", dummyPos),
                        dummyRange
                    )
                ), dummyRange
            )
        )

        val iterator = TestIterator(astNodes)
        val executor = Executor(fakeInputProvider, fakePrinter, fakeErrorHandler)
        val validator = Validator(fakeErrorHandler)
        val supportedNodes = setOf(
            NumberExpression::class.java,
            BinaryExpression::class.java,
            PrintFunction::class.java
        )

        val interpreter = Interpreter(iterator, validator, executor, supportedNodes)
        val results = interpreter.run()

        assertEquals(listOf("10.2", "20", "50.0"), printed)
        assertTrue(errors.isEmpty())
        assertEquals(3, results.size)
    }

    @Test
    fun `should declare variable and print its value`() {
        val printed = mutableListOf<String>()
        val errors = mutableListOf<String>()

        val fakePrinter = object : OutputPrinter {
            override fun print(value: String) { printed.add(value) }
        }

        val fakeErrorHandler = object : ErrorHandler {
            override fun handleError(message: String) { errors.add(message) }
        }

        val fakeInputProvider = object : InputProvider {
            override fun readInput(prompt: String) = "ignored"
        }

        val dummyPos = Position(0, 0)
        val dummyRange = Range(dummyPos, dummyPos)

        val astNodes: List<ASTNode> = listOf(
            VariableDeclarator(
                SymbolExpression("x", dummyPos),
                org.example.common.enums.Type.NUMBER,
                dummyRange,
                OptionalExpression.HasExpression(NumberExpression("42", dummyPos))
            ),
            PrintFunction(
                OptionalExpression.HasExpression(SymbolExpression("x", dummyPos)),
                dummyRange
            )
        )

        val iterator = TestIterator(astNodes)
        val executor = Executor(fakeInputProvider, fakePrinter, fakeErrorHandler)
        val validator = Validator(fakeErrorHandler)
        val supportedNodes = setOf(
            VariableDeclarator::class.java,
            PrintFunction::class.java,
            SymbolExpression::class.java,
            NumberExpression::class.java
        )

        val interpreter = Interpreter(iterator, validator, executor, supportedNodes)
        interpreter.run()

        assertEquals(listOf("42"), printed)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `should report error when using undefined symbol`() {
        val printed = mutableListOf<String>()
        val errors = mutableListOf<String>()

        val fakePrinter = object : OutputPrinter {
            override fun print(value: String) { printed.add(value) }
        }

        val fakeErrorHandler = object : ErrorHandler {
            override fun handleError(message: String) { errors.add(message) }
        }

        val fakeInputProvider = object : InputProvider {
            override fun readInput(prompt: String) = "ignored"
        }

        val dummyPos = Position(0, 0)
        val dummyRange = Range(dummyPos, dummyPos)

        val astNodes: List<ASTNode> = listOf(
            PrintFunction(
                OptionalExpression.HasExpression(SymbolExpression("y", dummyPos)),
                dummyRange
            )
        )

        val iterator = TestIterator(astNodes)
        val executor = Executor(fakeInputProvider, fakePrinter, fakeErrorHandler)
        val validator = Validator(fakeErrorHandler)
        val supportedNodes = setOf(
            PrintFunction::class.java,
            SymbolExpression::class.java
        )

        val interpreter = Interpreter(iterator, validator, executor, supportedNodes)
        interpreter.run()

        assertTrue(printed.isEmpty())
        assertEquals(1, errors.size)
        assertTrue(errors.first().contains("Undefined symbol") || errors.first().contains("null"))
    }
}
