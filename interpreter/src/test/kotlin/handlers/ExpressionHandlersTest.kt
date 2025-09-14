package handlers

import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.StringExpression
import org.example.common.Position
import org.example.common.enums.Type
import org.example.interpreter.Validator
import org.example.interpreter.ast_handlers.BooleanExpressionHandler
import org.example.interpreter.ast_handlers.StringExpressionHandler
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.output.ErrorHandler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExpressionHandlersTest {

    private lateinit var booleanHandler: BooleanExpressionHandler
    private lateinit var stringHandler: StringExpressionHandler

    private val fakeErrorHandler = object : ErrorHandler {
        val errors = mutableListOf<String>()
        override fun handleError(message: String) { errors.add(message) }
    }

    private lateinit var validator: Validator

    @BeforeEach
    fun setup() {
        booleanHandler = BooleanExpressionHandler()
        stringHandler = StringExpressionHandler()

        val handlers: Map<Class<out org.example.ast.ASTNode>, ASTNodeHandler<*>> = emptyMap()
        validator = Validator(handlers, fakeErrorHandler)
    }

    @Test
    fun `BooleanExpressionHandler should push Type BOOLEAN on validation`() {
        booleanHandler.handleValidation(BooleanExpression("true", Position(0, 0)), validator)
        val type = validator.popLiteral()
        assertEquals(Type.BOOLEAN, type)
    }

    @Test
    fun `StringExpressionHandler should push Type STRING on validation`() {
        stringHandler.handleValidation(StringExpression("hello", Position(0,0)), validator)
        val type = validator.popLiteral()
        assertEquals(Type.STRING, type)
    }
}
