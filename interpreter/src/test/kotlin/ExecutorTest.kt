import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.ReadEnvExpression
import org.example.ast.expressions.ReadInputExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.Condition
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.ErrorHandler
import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Variable
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.OutputPrinter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExecutorTest {

    private val dummyPos = Position(0, 0)
    private val dummyRange = Range(dummyPos, dummyPos)

    private val fakeInputProvider = object : InputProvider {
        override fun readInput(prompt: String) = "42"
    }

    private val printed = mutableListOf<String>()
    private val fakePrinter = object : OutputPrinter {
        override fun print(value: String) { printed.add(value) }
    }

    private val errors = mutableListOf<String>()
    private val fakeErrorHandler = object : ErrorHandler {
        override fun handleError(message: String) { errors.add(message) }
    }

    private val executor = Executor(fakeInputProvider, fakePrinter, fakeErrorHandler)

    @Test
    fun `visitNumber returns correct value`() {
        val node = NumberExpression("7", dummyPos)
        val result = executor.evaluate(node)
        assertEquals(7, result)
    }

    @Test
    fun `visitString returns correct value`() {
        val node = StringExpression("hello", dummyPos)
        val result = executor.evaluate(node)
        assertEquals("hello", result)
    }

    @Test
    fun `visitBoolean returns correct value`() {
        val node = BooleanExpression("true", dummyPos)
        val result = executor.evaluate(node)
        assertEquals(true, result)
    }

    @Test
    fun `visitBinary adds numbers`() {
        val expr = BinaryExpression(
            NumberExpression("2", dummyPos),
            Operator.ADD,
            NumberExpression("3", dummyPos),
            dummyRange
        )
        val result = executor.evaluate(expr)
        assertEquals(5, result)
    }

    @Test
    fun `visitBinary concatenates non-numbers`() {
        val expr = BinaryExpression(
            StringExpression("hi", dummyPos),
            Operator.ADD,
            StringExpression(" there", dummyPos),
            dummyRange
        )
        val result = executor.evaluate(expr)
        assertEquals("hi there", result)
    }

    @Test
    fun `visitVariableDeclarator declares variable`() {
        val node = VariableDeclarator(
            symbol = SymbolExpression("x", dummyPos),
            type = Type.NUMBER,
            range = dummyRange,
            value = OptionalExpression.HasExpression(NumberExpression("7", dummyPos))
        )
        executor.evaluate(node)
        val variable = executor.lookupVariable("x") as? Variable
        assertEquals(7, variable?.value)
        assertEquals(7, variable?.value)
    }

    @Test
    fun `visitVariableAssigner assigns variable`() {
        val declarator = VariableDeclarator(
            symbol = SymbolExpression("y", dummyPos),
            type = Type.NUMBER,
            range = dummyRange,
            value = OptionalExpression.HasExpression(NumberExpression("3", dummyPos))
        )
        executor.evaluate(declarator)

        val assigner = VariableAssigner(
            symbol = SymbolExpression("y", dummyPos),
            value = OptionalExpression.HasExpression(NumberExpression("10", dummyPos)),
            range = dummyRange
        )
        executor.evaluate(assigner)

        val variable = executor.lookupVariable("y") as? Variable
        assertEquals(10, variable?.value)
    }

    @Test
    fun `visitPrintFunction prints value`() {
        val node = PrintFunction(
            value = OptionalExpression.HasExpression(StringExpression("Hello", dummyPos)),
            range = dummyRange
        )
        executor.evaluate(node)
        assertTrue(printed.contains("Hello"))
    }

    @Test
    fun `visitReadInput reads value`() {
        val node = ReadInputExpression(OptionalExpression.NoExpression, dummyRange)
        val result = executor.evaluate(node)
        assertEquals(42, result)
    }

    @Test
    fun `visitReadEnv reads environment variable`() {
        val envVarName = "PATH"
        val node = ReadEnvExpression(
            OptionalExpression.HasExpression(StringExpression(envVarName, dummyPos)),
            dummyRange
        )
        val result = executor.evaluate(node)
        assertNotNull(result)
    }

    @Test
    fun `visitCondition executes if and else blocks`() {
        val dummyPos = Position(0, 0)
        val dummyRange = Range(dummyPos, dummyPos)

        val ifNode = NumberExpression("1", dummyPos)
        val elseNode = NumberExpression("2", dummyPos)

        val cond = Condition(
            condition = BooleanExpression("true", dummyPos),
            ifBlock = listOf(ifNode),
            elseBlock = listOf(elseNode),
            range = dummyRange
        )

        executor.evaluate(cond)

        val result = executor.popLiteral() as Number

        if (result is Int) {
            assertEquals(1, result)
        } else if (result is Double) {
            assertEquals(1.0, result)
        } else {
            throw AssertionError("Unexpected type: ${result::class}")
        }
    }

    @Test
    fun `visitVariableImmutableDeclarator declares immutable variable`() {
        val node = VariableImmutableDeclarator(
            symbol = SymbolExpression("z", dummyPos),
            Type.NUMBER,
            value = OptionalExpression.HasExpression(NumberExpression("10", dummyPos)),
            range = dummyRange
        )

        executor.evaluate(node)

        val variable = executor.lookupVariable("z") as? Variable

        assertNotNull(variable)
        assertEquals(10, variable?.value)
        assertTrue(variable?.immutable == true)

        executor.assignVariable("z", 20)

        val variableAfterAssign = executor.lookupVariable("z") as? Variable
        assertEquals(10, variableAfterAssign?.value)
        assertTrue(errors.isNotEmpty())
        assertTrue(errors.any { it.contains("Immutable variable z already assigned") })
    }

    @Test
    fun `visitReadInput handles boolean input`() {
        val node = ReadInputExpression(OptionalExpression.NoExpression, dummyRange)
        val result = executor.evaluate(node)
        assertEquals(42, result)
    }

    @Test
    fun `visitReadInput parses true and false correctly`() {
        val booleanProvider = object : InputProvider {
            override fun readInput(prompt: String) = "true"
        }
        val exec = Executor(booleanProvider, fakePrinter, fakeErrorHandler)
        val node = ReadInputExpression(OptionalExpression.NoExpression, dummyRange)
        val result = exec.evaluate(node)
        assertEquals(true, result)

        val falseProvider = object : InputProvider {
            override fun readInput(prompt: String) = "false"
        }
        val execFalse = Executor(falseProvider, fakePrinter, fakeErrorHandler)
        val resultFalse = execFalse.evaluate(node)
        assertEquals(false, resultFalse)
    }

    @Test
    fun `visitReadInput parses integer and double correctly`() {
        val intProvider = object : InputProvider {
            override fun readInput(prompt: String) = "123"
        }
        val execInt = Executor(intProvider, fakePrinter, fakeErrorHandler)
        val node = ReadInputExpression(OptionalExpression.NoExpression, dummyRange)
        val resultInt = execInt.evaluate(node)
        assertEquals(123, resultInt)

        val doubleProvider = object : InputProvider {
            override fun readInput(prompt: String) = "3.14"
        }
        val execDouble = Executor(doubleProvider, fakePrinter, fakeErrorHandler)
        val resultDouble = execDouble.evaluate(node)
        assertEquals(3.14, resultDouble)
    }

    @Test
    fun `visitReadInput returns string when input is non-numeric`() {
        val strProvider = object : InputProvider {
            override fun readInput(prompt: String) = "hello"
        }
        val execStr = Executor(strProvider, fakePrinter, fakeErrorHandler)
        val node = ReadInputExpression(OptionalExpression.NoExpression, dummyRange)
        val result = execStr.evaluate(node)
        assertEquals("hello", result)
    }

    @Test
    fun `visitReadInput handles empty input`() {
        val emptyProvider = object : InputProvider {
            override fun readInput(prompt: String) = ""
        }
        val execEmpty = Executor(emptyProvider, fakePrinter, fakeErrorHandler)
        val node = ReadInputExpression(OptionalExpression.NoExpression, dummyRange)
        val result = execEmpty.evaluate(node)
        assertEquals("", result)
    }

    @Test
    fun `visitReadInput handles prompt expression`() {
        val promptProvider = object : InputProvider {
            override fun readInput(prompt: String): String {
                assertEquals("Enter value:", prompt)
                return "42"
            }
        }
        val execPrompt = Executor(promptProvider, fakePrinter, fakeErrorHandler)
        val node = ReadInputExpression(
            OptionalExpression.HasExpression(StringExpression("Enter value:", dummyPos)),
            dummyRange
        )
        val result = execPrompt.evaluate(node)
        assertEquals(42, result)
    }

    @Test
    fun `visitSymbol returns null for non-existent variable`() {
        val symbolNode = SymbolExpression("nonExistentVar", dummyPos)

        executor.evaluate(symbolNode)

        val result = executor.popLiteral()
        assertNull(result)
    }
}
