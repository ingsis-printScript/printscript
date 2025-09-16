import org.example.ast.expressions.*
import org.example.ast.statements.*
import org.example.common.ErrorHandler
import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.interpreter.Validator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ValidatorTest {

    private val dummyPos = Position(0, 0)
    private val dummyRange = Range(dummyPos, dummyPos)

    class FakeErrorHandler : ErrorHandler {
        val errors = mutableListOf<String>()
        override fun handleError(message: String) {
            errors.add(message)
        }
    }

    @Test
    fun `should declare variable and infer type`() {
        val errorHandler = FakeErrorHandler()
        val validator = Validator(errorHandler)

        val node = VariableDeclarator(
            SymbolExpression("x", dummyPos),
            Type.NUMBER,
            dummyRange,
            OptionalExpression.HasExpression(NumberExpression("42", dummyPos))
        )

        validator.evaluate(node)

        val type = validator.lookupSymbol("x")
        assertEquals(Type.NUMBER, type)
        assertTrue(errorHandler.errors.isEmpty())
    }

    @Test
    fun `should declare immutable variable and infer type`() {
        val errorHandler = FakeErrorHandler()
        val validator = Validator(errorHandler)

        val node = VariableImmutableDeclarator(
            SymbolExpression("y", dummyPos),
            Type.STRING,
            dummyRange,
            OptionalExpression.HasExpression(StringExpression("\"hello\"", dummyPos))
        )

        validator.evaluate(node)

        val type = validator.lookupSymbol("y")
        assertEquals(Type.STRING, type)
        assertTrue(errorHandler.errors.isEmpty())
    }

    @Test
    fun `should assign variable if declared`() {
        val errorHandler = FakeErrorHandler()
        val validator = Validator(errorHandler)

        // Declarar primero
        validator.evaluate(
            VariableDeclarator(
                SymbolExpression("x", dummyPos),
                Type.NUMBER,
                dummyRange,
                OptionalExpression.NoExpression
            )
        )

        // Asignar
        val assign = VariableAssigner(
            SymbolExpression("x", dummyPos),
            OptionalExpression.HasExpression(NumberExpression("10", dummyPos)),
            dummyRange
        )

        val resultType = validator.evaluate(assign)
        assertEquals(Type.NUMBER, resultType)
        assertTrue(errorHandler.errors.isEmpty())
    }

    @Test
    fun `should report error when assigning undeclared variable`() {
        val errorHandler = FakeErrorHandler()
        val validator = Validator(errorHandler)

        val assign = VariableAssigner(
            SymbolExpression("z", dummyPos),
            OptionalExpression.HasExpression(NumberExpression("10", dummyPos)),
            dummyRange
        )

        validator.evaluate(assign)

        assertEquals(1, errorHandler.errors.size)
        assertTrue(errorHandler.errors.first().contains("not declared"))
    }

    @Test
    fun `should evaluate binary expressions correctly`() {
        val errorHandler = FakeErrorHandler()
        val validator = Validator(errorHandler)

        val binary = BinaryExpression(
            NumberExpression("5", dummyPos),
            Operator.ADD,
            NumberExpression("10", dummyPos),
            dummyRange
        )

        val type = validator.evaluate(binary)
        assertEquals(Type.NUMBER, type)
        assertTrue(errorHandler.errors.isEmpty())
    }

    @Test
    fun `should report error for invalid binary types`() {
        val errorHandler = FakeErrorHandler()
        val validator = Validator(errorHandler)

        val binary = BinaryExpression(
            StringExpression("\"hi\"", dummyPos),
            Operator.SUB,
            NumberExpression("10", dummyPos),
            dummyRange
        )

        validator.evaluate(binary)
        assertEquals(1, errorHandler.errors.size)
        assertTrue(errorHandler.errors.first().contains("requires numeric operands"))
    }

    @Test
    fun `should handle symbol lookup`() {
        val errorHandler = FakeErrorHandler()
        val validator = Validator(errorHandler)

        validator.evaluate(
            VariableDeclarator(
                SymbolExpression("x", dummyPos),
                Type.NUMBER,
                dummyRange,
                OptionalExpression.NoExpression
            )
        )

        val symbolExpr = SymbolExpression("x", dummyPos)
        val type = validator.evaluate(symbolExpr)
        assertEquals(Type.NUMBER, type)
    }

    @Test
    fun `should infer boolean and string types`() {
        val errorHandler = FakeErrorHandler()
        val validator = Validator(errorHandler)

        assertEquals(Type.BOOLEAN, validator.evaluate(BooleanExpression("true", dummyPos)))
        assertEquals(Type.STRING, validator.evaluate(StringExpression("\"hello\"", dummyPos)))
    }
}
