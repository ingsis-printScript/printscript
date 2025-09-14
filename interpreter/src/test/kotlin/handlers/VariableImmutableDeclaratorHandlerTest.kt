package handlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.common.ErrorHandler
import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.asthandlers.VariableImmutableDeclaratorHandler
import org.example.interpreter.handlers.ASTNodeHandler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VariableImmutableDeclaratorHandlerTest {

    private val fakeErrorHandler = object : ErrorHandler {
        val errors = mutableListOf<String>()
        override fun handleError(message: String) {
            errors.add(message)
        }
    }

    private val handlers: Map<Class<out org.example.ast.ASTNode>, ASTNodeHandler<*>> = mapOf(
        VariableImmutableDeclarator::class.java to VariableImmutableDeclaratorHandler(),
        SymbolExpression::class.java to object : ASTNodeHandler<SymbolExpression> {
            override fun handleExecution(node: SymbolExpression, executor: Executor) {}
            override fun handleValidation(node: SymbolExpression, validator: Validator) {
                val type = when (node.value) {
                    "true", "false" -> Type.BOOLEAN
                    else -> Type.NUMBER
                }
                validator.pushLiteral(type)
            }
        }
    )

    private val validator = Validator(handlers, fakeErrorHandler)

    @Test
    fun `should validate correct variable declaration`() {
        val node = VariableImmutableDeclarator(
            SymbolExpression("x", Position(0, 0)),
            Type.NUMBER,
            Range(Position(0, 0), Position(0, 1)),
            OptionalExpression.HasExpression(SymbolExpression("5", Position(0, 0)))
        )

        val handler = handlers[VariableImmutableDeclarator::class.java] as VariableImmutableDeclaratorHandler
        handler.handleValidation(node, validator)

        val type = validator.lookupSymbol("x")
        assertEquals(Type.NUMBER, type)
        assertTrue(fakeErrorHandler.errors.isEmpty())
    }

    @Test
    fun `should throw error when redeclaring variable`() {
        val node1 = VariableImmutableDeclarator(
            SymbolExpression("x", Position(0, 0)),
            Type.NUMBER,
            Range(Position(0, 0), Position(0, 1)),
            OptionalExpression.HasExpression(SymbolExpression("5", Position(0, 0)))
        )

        val node2 = VariableImmutableDeclarator(
            SymbolExpression("x", Position(1, 0)),
            Type.NUMBER,
            Range(Position(1, 0), Position(1, 1)),
            OptionalExpression.NoExpression
        )

        val handler = handlers[VariableImmutableDeclarator::class.java] as VariableImmutableDeclaratorHandler
        handler.handleValidation(node1, validator)

        val exception = assertThrows<RuntimeException> {
            handler.handleValidation(node2, validator)
        }
        assertEquals("Variable x ya declarada", exception.message)
    }

    @Test
    fun `should report type mismatch error`() {
        val node = VariableImmutableDeclarator(
            SymbolExpression("x", Position(0, 0)),
            Type.NUMBER,
            Range(Position(0, 0), Position(0, 1)),
            OptionalExpression.HasExpression(SymbolExpression("true", Position(0, 0))) // boolean en vez de number
        )

        val handler = handlers[VariableImmutableDeclarator::class.java] as VariableImmutableDeclaratorHandler
        handler.handleValidation(node, validator)

        assertTrue(fakeErrorHandler.errors.isNotEmpty())
        assertTrue(fakeErrorHandler.errors[0].contains("Type mismatch"))
    }
}
