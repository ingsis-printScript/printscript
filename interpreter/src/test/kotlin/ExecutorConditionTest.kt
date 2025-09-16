package interpreter


import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.Condition
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.ErrorHandler
import org.example.common.Position
import org.example.common.Range
import org.example.interpreter.Executor
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.OutputPrinter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExecutorConditionTest {

    private val dummyPos = Position(0, 0)
    private val dummyRange = Range(dummyPos, dummyPos)

    @Test
    fun `should execute if block when condition is true`() {
        val printed = mutableListOf<String>()

        val executor = Executor(
            inputProvider = object : InputProvider {
                override fun readInput(prompt: String) = "ignored"
            },
            outputPrinter = object : OutputPrinter {
                override fun print(value: String) { printed.add(value) }
            },
            errorHandler = object : ErrorHandler {
                override fun handleError(message: String) { printed.add("ERROR: $message") }
            }
        )

        val condition = Condition(
            condition = BooleanExpression("true", dummyPos), // condición booleana directa
            ifBlock = listOf(
                PrintFunction(
                    OptionalExpression.HasExpression(NumberExpression("42", dummyPos)), // imprime 42
                    dummyRange
                )
            ),
            elseBlock = listOf(
                PrintFunction(
                    OptionalExpression.HasExpression(NumberExpression("0", dummyPos)), // imprime 0
                    dummyRange
                )
            ),
            range = dummyRange
        )

        executor.visitCondition(condition)
        assertEquals(listOf("42"), printed)
    }

    @Test
    fun `should execute else block when condition is false`() {
        val printed = mutableListOf<String>()

        val executor = Executor(
            inputProvider = object : InputProvider {
                override fun readInput(prompt: String) = "ignored"
            },
            outputPrinter = object : OutputPrinter {
                override fun print(value: String) { printed.add(value) }
            },
            errorHandler = object : ErrorHandler {
                override fun handleError(message: String) { printed.add("ERROR: $message") }
            }
        )

        val condition = Condition(
            condition = BooleanExpression("false", dummyPos), // condición booleana directa
            ifBlock = listOf(
                PrintFunction(
                    OptionalExpression.HasExpression(NumberExpression("42", dummyPos)),
                    dummyRange
                )
            ),
            elseBlock = listOf(
                PrintFunction(
                    OptionalExpression.HasExpression(NumberExpression("0", dummyPos)),
                    dummyRange
                )
            ),
            range = dummyRange
        )

        executor.visitCondition(condition)
        assertEquals(listOf("0"), printed)
    }


    @Test
    fun `visitCondition executes only the correct block`() {
        val printed = mutableListOf<String>()
        val errors = mutableListOf<String>()

        val fakePrinter = object : OutputPrinter {
            override fun print(value: String) {
                printed.add(value)
            }
        }

        val fakeErrorHandler = object : ErrorHandler {
            override fun handleError(message: String) {
                errors.add(message)
            }
        }

        val fakeInputProvider = object : InputProvider {
            override fun readInput(prompt: String) = ""
        }

        val executor = Executor(fakeInputProvider, fakePrinter, fakeErrorHandler)
        val dummyPos = Position(0, 0)
        val dummyRange = Range(dummyPos, dummyPos)

        // ifBlock que NO debería ejecutarse
        val ifNode = PrintFunction(
            OptionalExpression.HasExpression(StringExpression("if statement is not working correctly", dummyPos)),
            dummyRange
        )

        // println fuera del condicional
        val outsideNode = PrintFunction(
            OptionalExpression.HasExpression(StringExpression("outside of conditional", dummyPos)),
            dummyRange
        )

        // Condicional con false
        val conditionNode = Condition(
            condition = BooleanExpression("false", dummyPos),
            ifBlock = listOf(ifNode),
            elseBlock = null,
            range = dummyRange
        )

        // Ejecutamos condicional y luego la línea fuera del condicional
        executor.evaluate(conditionNode)
        executor.evaluate(outsideNode)

        // Solo debería imprimir fuera del condicional
        assertEquals(listOf("outside of conditional"), printed)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `visitCondition prints only expected lines`() {
        val printed = mutableListOf<String>()
        val errors = mutableListOf<String>()

        val fakePrinter = object : OutputPrinter {
            override fun print(value: String) {
                printed.add(value)
            }
        }

        val fakeErrorHandler = object : ErrorHandler {
            override fun handleError(message: String) {
                errors.add(message)
            }
        }

        val fakeInputProvider = object : InputProvider {
            override fun readInput(prompt: String) = ""
        }

        val executor = Executor(fakeInputProvider, fakePrinter, fakeErrorHandler)
        val dummyPos = Position(0, 0)
        val dummyRange = Range(dummyPos, dummyPos)

        // Bloque if que NO debería ejecutarse
        val ifNode = PrintFunction(
            OptionalExpression.HasExpression(StringExpression("if statement is not working correctly", dummyPos)),
            dummyRange
        )

        // Línea fuera del condicional
        val outsideNode = PrintFunction(
            OptionalExpression.HasExpression(StringExpression("outside of conditional", dummyPos)),
            dummyRange
        )

        // Condicional con false
        val conditionNode = Condition(
            condition = BooleanExpression("false", dummyPos),
            ifBlock = listOf(ifNode),
            elseBlock = null,
            range = dummyRange
        )

        // Ejecutamos condicional y luego la línea fuera del condicional
        executor.evaluate(conditionNode)
        executor.evaluate(outsideNode)

        println("DEBUG printed lines: $printed") // <-- imprime todo lo que se imprimió

        // Solo debería imprimirse fuera del condicional
        assertEquals(listOf("outside of conditional"), printed)
        assertTrue(errors.isEmpty())
    }


}
