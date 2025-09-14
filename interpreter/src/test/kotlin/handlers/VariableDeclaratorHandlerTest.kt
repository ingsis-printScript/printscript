package handlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableDeclarator
import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.Type
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.ast_handlers.VariableDeclaratorHandler
import org.example.interpreter.output.ErrorHandler
import org.example.interpreter.Executor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VariableDeclaratorHandlerTest {

    private val fakeErrorHandler = object : ErrorHandler {
        val errors = mutableListOf<String>()
        override fun handleError(message: String) {
            errors.add(message)
        }
    }

    private val handlers: Map<Class<out org.example.ast.ASTNode>, ASTNodeHandler<*>> = mapOf(
        VariableDeclarator::class.java to VariableDeclaratorHandler(),
        SymbolExpression::class.java to object : ASTNodeHandler<SymbolExpression> {
            override fun handleExecution(node: SymbolExpression, executor: Executor) {}
            override fun handleValidation(node: SymbolExpression, validator: Validator) {
                when (node.value) {
                    "true", "false" -> validator.pushLiteral(Type.BOOLEAN)
                    else -> validator.pushLiteral(Type.NUMBER)
                }
            }
        }
    )

    private val validator = Validator(handlers, fakeErrorHandler)

    @Test
    fun `should validate correct variable declaration`() {
        val node = VariableDeclarator(
            SymbolExpression("x", Position(0, 0)),
            Type.NUMBER,
            Range(Position(0, 0), Position(0, 1)),
            OptionalExpression.HasExpression(SymbolExpression("5", Position(0, 0)))
        )

        val handler = handlers[VariableDeclarator::class.java] as VariableDeclaratorHandler
        handler.handleValidation(node, validator)

        val type = validator.lookupSymbol("x")
        assertEquals(Type.NUMBER, type)
        assertTrue(fakeErrorHandler.errors.isEmpty())
    }

    @Test
    fun `should throw error when redeclaring variable`() {
        val node1 = VariableDeclarator(
            SymbolExpression("x", Position(0, 0)),
            Type.NUMBER,
            Range(Position(0, 0), Position(0, 1)),
            OptionalExpression.HasExpression(SymbolExpression("5", Position(0, 0)))
        )

        val node2 = VariableDeclarator(
            SymbolExpression("x", Position(1, 0)),
            Type.NUMBER,
            Range(Position(1, 0), Position(1, 1)),
            OptionalExpression.NoExpression
        )

        val handler = handlers[VariableDeclarator::class.java] as VariableDeclaratorHandler
        handler.handleValidation(node1, validator)

        val exception = assertThrows<RuntimeException> {
            handler.handleValidation(node2, validator)
        }
        assertEquals("Variable x ya declarada", exception.message)
    }

    @Test
    fun `should report type mismatch error for mutable variable`() {
        val node = VariableDeclarator(
            SymbolExpression("x", Position(0,0)),
            Type.NUMBER,
            Range(Position(0,0), Position(0,1)),
            OptionalExpression.HasExpression(SymbolExpression("true", Position(0,0))) // boolean en vez de number
        )

        val handler = handlers[VariableDeclarator::class.java] as VariableDeclaratorHandler
        handler.handleValidation(node, validator)

        assertTrue(fakeErrorHandler.errors.isNotEmpty())
        assertTrue(fakeErrorHandler.errors[0].contains("Type mismatch"))
    }
}
