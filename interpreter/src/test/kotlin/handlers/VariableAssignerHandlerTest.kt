package handlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.asthandlers.VariableAssignerHandler
import org.example.interpreter.asthandlers.VariableImmutableDeclaratorHandler
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.ErrorHandler
import org.example.interpreter.output.OutputPrinter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VariableAssignerHandlerTest {

    private val fakeErrorHandler = object : ErrorHandler {
        val errors = mutableListOf<String>()
        override fun handleError(message: String) {
            errors.add(message)
        }
    }

    private val fakeInputProvider = object : InputProvider {
        override fun readInput(prompt: String): String = "5"
    }

    private val fakeOutputPrinter = object : OutputPrinter {
        val printed = mutableListOf<String>()
        override fun print(message: String) {
            printed.add(message)
        }
    }

    private val handlers: Map<Class<out org.example.ast.ASTNode>, ASTNodeHandler<*>> = mapOf(
        VariableAssigner::class.java to VariableAssignerHandler(),
        SymbolExpression::class.java to object : ASTNodeHandler<SymbolExpression> {
            override fun handleExecution(node: SymbolExpression, executor: Executor) {
                // Simulamos devolver el valor correcto según el nombre
                val value = when (node.value) {
                    "5" -> 5
                    "10" -> 10
                    "true" -> true
                    "false" -> false
                    else -> 0
                }
                executor.pushLiteral(value)
            }

            override fun handleValidation(node: SymbolExpression, validator: Validator) {
                val type = when (node.value) {
                    "5", "10" -> Type.NUMBER
                    "true", "false" -> Type.BOOLEAN
                    else -> Type.NUMBER
                }
                validator.pushLiteral(type)
            }
        }
    )

    private val validator = Validator(handlers, fakeErrorHandler)

    @Test
    fun `should assign value to existing variable`() {
        val declarator = VariableImmutableDeclarator(
            SymbolExpression("x", Position(0, 0)),
            Type.NUMBER,
            Range(Position(0, 0), Position(0, 1)),
            OptionalExpression.HasExpression(SymbolExpression("5", Position(0, 0)))
        )
        val declaratorHandler = VariableImmutableDeclaratorHandler()
        declaratorHandler.handleValidation(declarator, validator)

        val assigner = VariableAssigner(
            SymbolExpression("x", Position(1, 0)),
            OptionalExpression.HasExpression(SymbolExpression("10", Position(1, 0))),
            Range(Position(1, 0), Position(1, 1))
        )
        val handler = handlers[VariableAssigner::class.java] as VariableAssignerHandler
        handler.handleValidation(assigner, validator)

        val type = validator.popLiteral()
        assertEquals(Type.NUMBER, type)
    }

    @Test
    fun `should throw error if type mismatch`() {
        val declarator = VariableImmutableDeclarator(
            SymbolExpression("x", Position(0, 0)),
            Type.NUMBER,
            Range(Position(0, 0), Position(0, 1)),
            OptionalExpression.HasExpression(SymbolExpression("5", Position(0, 0)))
        )
        val declaratorHandler = VariableImmutableDeclaratorHandler()
        declaratorHandler.handleValidation(declarator, validator)

        val assigner = VariableAssigner(
            SymbolExpression("x", Position(1, 0)),
            OptionalExpression.HasExpression(SymbolExpression("true", Position(1, 0))),
            Range(Position(1, 0), Position(1, 1))
        )
        val handler = handlers[VariableAssigner::class.java] as VariableAssignerHandler
        val exception = assertThrows<RuntimeException> {
            handler.handleValidation(assigner, validator)
        }
        assertTrue(exception.message!!.contains("Type mismatch"))
    }

    @Test
    fun `should throw error if no expression`() {
        val assigner = VariableAssigner(
            SymbolExpression("x", Position(0, 0)),
            OptionalExpression.NoExpression,
            Range(Position(0, 0), Position(0, 1))
        )
        val handler = handlers[VariableAssigner::class.java] as VariableAssignerHandler
        val exception = assertThrows<RuntimeException> {
            handler.handleValidation(assigner, validator)
        }
        assertTrue(exception.message!!.contains("Missing expression"))
    }

    @Test
    fun `execute VariableAssigner updates environment`() {
        val executor = Executor(handlers, fakeInputProvider, fakeOutputPrinter, fakeErrorHandler)

        // Primero declaramos la variable con su handler de declarador
        val declarator = VariableImmutableDeclarator(
            SymbolExpression("x", Position(0, 0)),
            Type.NUMBER,
            Range(Position(0, 0), Position(0, 1)),
            OptionalExpression.HasExpression(SymbolExpression("5", Position(0, 0)))
        )
        val declaratorHandler = VariableImmutableDeclaratorHandler()
        declaratorHandler.handleExecution(declarator, executor)

        // Luego asignamos un nuevo valor
        val assigner = VariableAssigner(
            SymbolExpression("x", Position(1, 0)),
            OptionalExpression.HasExpression(SymbolExpression("10", Position(1, 0))),
            Range(Position(1, 0), Position(1, 1))
        )
        val handler = handlers[VariableAssigner::class.java] as VariableAssignerHandler
        handler.handleExecution(assigner, executor)

        assertEquals(10, executor.lookupVariable("x"))
    }
}
