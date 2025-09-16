import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.ReadEnvExpression
import org.example.ast.expressions.ReadInputExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.common.ErrorHandler
import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.interpreter.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
    private val errorHandler = FakeErrorHandler()

    private val validator = Validator(errorHandler)

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

        validator.evaluate(
            VariableDeclarator(
                SymbolExpression("x", dummyPos),
                Type.NUMBER,
                dummyRange,
                OptionalExpression.NoExpression
            )
        )

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

    @Test
    fun `visitReadInput should push STRING type`() {
        val node = ReadInputExpression(OptionalExpression.NoExpression, dummyRange)

        validator.visitReadInput(node)

        val result = validator.popLiteral()
        assertEquals(Type.STRING, result)
    }

    @Test
    fun `visitReadEnv without expression should push STRING type`() {
        val node = ReadEnvExpression(OptionalExpression.NoExpression, dummyRange)

        validator.visitReadEnv(node)

        val result = validator.popLiteral()
        assertEquals(Type.STRING, result)
    }

    @Test
    fun `visitReadEnv with expression should evaluate expression type`() {
        val expr = NumberExpression("123", dummyPos)
        val node = ReadEnvExpression(OptionalExpression.HasExpression(expr), dummyRange)

        validator.visitReadEnv(node)

        val result = validator.popLiteral()
        assertEquals(Type.NUMBER, result)
    }
}
