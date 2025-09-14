import org.example.ast.expressions.OptionalExpression
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.linter.Linter
import org.example.linter.provider.LinterProvider10
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class LinterTest {

    @TempDir
    lateinit var tempDir: Path
    private lateinit var linter: Linter
    private val astFactory = AstFactory()

    @BeforeEach
    fun setUp() {
        this.linter = LinterProvider10().provide()
    }

    @Test
    fun `should read JSON configuration correctly`() {
        // Config: { "identifier_format": "camel_case", "println_only_literals_and_identifiers": true }
        val configFile = createJsonConfig(
            mapOf(
                "identifier_format" to "camel_case",
                "println_only_literals_and_identifiers" to true
            )
        )

        // AST: Simple symbol "userName" (valid camelCase)
        val ast = astFactory.createSymbol("userName")

        val report = linter.analyze(ast, configFile)
        assertFalse(report.hasViolations())
    }

    @Test
    fun `should read YAML configuration correctly`() {
        // Config: identifier_format: snake_case, println_only_literals_and_identifiers: false
        val configFile = createYamlConfig(
            mapOf(
                "identifier_format" to "snake_case",
                "println_only_literals_and_identifiers" to false
            )
        )

        // AST: Simple symbol "user_name" (valid snake_case)
        val ast = astFactory.createSymbol("user_name")

        val report = linter.analyze(ast, configFile)
        assertFalse(report.hasViolations())
    }

    @Test
    fun `should throw exception for non-existent config file`() {
        // AST: Any simple AST
        val ast = astFactory.createSymbol("test")

        assertThrows(IllegalArgumentException::class.java) {
            linter.analyze(ast, "non-existent.json")
        }
    }

    // ========== SYMBOL FORMAT RULE TESTS ==========

    @Test
    fun `should accept valid camelCase identifiers`() {
        val configFile = createJsonConfig(mapOf("identifier_format" to "camel_case"))

        // camelCase let userName: string = "test";
        val symbol = astFactory.createSymbol("userName")
        val value = OptionalExpression.HasExpression(astFactory.createString("test"))
        val ast = astFactory.createVariableDeclarator(symbol, Type.STRING, value)

        val report = linter.analyze(ast, configFile)
        assertFalse(report.hasViolations())
    }

    @Test
    fun `should reject invalid camelCase identifiers`() {
        val configFile = createJsonConfig(mapOf("identifier_format" to "camel_case"))

        // AST: Variable declaration with snake_case name - let user_name: string = "test";
        val symbol = astFactory.createSymbol("user_name")
        val value = OptionalExpression.HasExpression(astFactory.createString("test"))
        val ast = astFactory.createVariableDeclarator(symbol, Type.STRING, value)

        val report = linter.analyze(ast, configFile)
        assertTrue(report.hasViolations())
        assertTrue(report.violations[0].message.contains("should be in camelCase format"))
    }

    @Test
    fun `should accept valid snake_case identifiers`() {
        val configFile = createJsonConfig(mapOf("identifier_format" to "snake_case"))

        // AST: Variable assignment - user_name = "test";
        val symbol = astFactory.createSymbol("user_name")
        val value = OptionalExpression.HasExpression(astFactory.createString("test"))
        val ast = astFactory.createVariableAssigment(symbol, value)

        val report = linter.analyze(ast, configFile)
        assertFalse(report.hasViolations())
    }

    @Test
    fun `should reject invalid snake_case identifiers`() {
        val configFile = createJsonConfig(mapOf("identifier_format" to "snake_case"))

        // AST: Variable assignment - userName = "test";
        val symbol = astFactory.createSymbol("userName")
        val value = OptionalExpression.HasExpression(astFactory.createString("test"))
        val ast = astFactory.createVariableAssigment(symbol, value)

        val report = linter.analyze(ast, configFile)
        assertTrue(report.hasViolations())
        assertTrue(report.violations[0].message.contains("should be in snake_case format"))
    }

    @Test
    fun `should check symbols in binary expressions`() {
        val configFile = createJsonConfig(mapOf("identifier_format" to "camel_case"))

        // AST: Binary expression - user_name + other_var (both invalid camelCase)
        val leftSymbol = astFactory.createSymbol("user_name")
        val rightSymbol = astFactory.createSymbol("other_var")
        val ast = astFactory.createBinaryExpression(leftSymbol, Operator.ADD, rightSymbol)

        val report = linter.analyze(ast, configFile)
        assertEquals(2, report.violations.size)
    }

    @Test
    fun `symbol format rule should be disabled when not configured`() {
        val configFile = createJsonConfig(emptyMap())

        // AST: Variable with any format - user_name (no rule configured, should pass)
        val symbol = astFactory.createSymbol("user_name")
        val ast = astFactory.createVariableDeclarator(symbol, Type.STRING)

        val report = linter.analyze(ast, configFile)
        assertFalse(report.hasViolations())
    }

    // ========== PRINT ARGUMENT RULE TESTS ==========

    @Test
    fun `should allow println with string literal`() {
        val configFile = createJsonConfig(mapOf("println_only_literals_and_identifiers" to true))

        // AST: println("hello world");
        val stringArg = astFactory.createString("hello world")
        val ast = astFactory.createPrintFunction(OptionalExpression.HasExpression(stringArg))

        val report = linter.analyze(ast, configFile)
        assertFalse(report.hasViolations())
    }

    @Test
    fun `should allow println with number literal`() {
        val configFile = createJsonConfig(mapOf("println_only_literals_and_identifiers" to true))

        // AST: println(42);
        val numberArg = astFactory.createNumber("42")
        val ast = astFactory.createPrintFunction(OptionalExpression.HasExpression(numberArg))

        val report = linter.analyze(ast, configFile)
        assertFalse(report.hasViolations())
    }

    @Test
    fun `should allow println with identifier`() {
        val configFile = createJsonConfig(mapOf("println_only_literals_and_identifiers" to true))

        // AST: println(variable);
        val symbolArg = astFactory.createSymbol("variable")
        val ast = astFactory.createPrintFunction(OptionalExpression.HasExpression(symbolArg))

        val report = linter.analyze(ast, configFile)
        assertFalse(report.hasViolations())
    }

    @Test
    fun `should reject println with binary expression`() {
        val configFile = createJsonConfig(mapOf("println_only_literals_and_identifiers" to true))

        // AST: println(a + b);
        val leftSymbol = astFactory.createSymbol("a")
        val rightSymbol = astFactory.createSymbol("b")
        val binaryExpr = astFactory.createBinaryExpression(leftSymbol, Operator.ADD, rightSymbol)
        val ast = astFactory.createPrintFunction(OptionalExpression.HasExpression(binaryExpr))

        val report = linter.analyze(ast, configFile)
        assertTrue(report.hasViolations())
        assertTrue(report.violations[0].message.contains("println() can not contain"))
    }

    @Test
    fun `should allow println with no arguments`() {
        val configFile = createJsonConfig(mapOf("println_only_literals_and_identifiers" to true))

        // AST: println();
        val ast = astFactory.createPrintFunction(OptionalExpression.NoExpression)

        val report = linter.analyze(ast, configFile)
        assertFalse(report.hasViolations())
    }

    @Test
    fun `print argument rule should be disabled when not configured`() {
        val configFile = createJsonConfig(mapOf("println_only_literals_and_identifiers" to false))

        // AST: println(a + b); (should pass since rule is disabled)
        val leftSymbol = astFactory.createSymbol("a")
        val rightSymbol = astFactory.createSymbol("b")
        val binaryExpr = astFactory.createBinaryExpression(leftSymbol, Operator.ADD, rightSymbol)
        val ast = astFactory.createPrintFunction(OptionalExpression.HasExpression(binaryExpr))

        val report = linter.analyze(ast, configFile)
        assertFalse(report.hasViolations())
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    fun `should apply multiple rules simultaneously`() {
        val configFile = createJsonConfig(
            mapOf(
                "identifier_format" to "camel_case",
                "println_only_literals_and_identifiers" to true
            )
        )

        // AST: println(user_name + other_var);
        val leftSymbol = astFactory.createSymbol("user_name")
        val rightSymbol = astFactory.createSymbol("other_var")
        val binaryExpr = astFactory.createBinaryExpression(leftSymbol, Operator.ADD, rightSymbol)
        val ast = astFactory.createPrintFunction(OptionalExpression.HasExpression(binaryExpr))

        val report = linter.analyze(ast, configFile)
        assertEquals(3, report.violations.size)
    }

    @Test
    fun `should handle complex AST with nested violations`() {
        val configFile = createJsonConfig(
            mapOf(
                "identifier_format" to "snake_case",
                "println_only_literals_and_identifiers" to true
            )
        )

        // let userName: string = "test";
        // userName = userName + someVar;
        // println(userName + someVar);

        val symbol1 = astFactory.createSymbol("userName")
        val value1 = OptionalExpression.HasExpression(astFactory.createString("test"))
        val declaration = astFactory.createVariableDeclarator(symbol1, Type.STRING, value1)

        // Then create assignment with binary expression
        val symbol2 = astFactory.createSymbol("userName")
        val symbol3 = astFactory.createSymbol("someVar")
        val binaryExpr = astFactory.createBinaryExpression(symbol2, Operator.ADD, symbol3)
        val assignment = astFactory.createVariableAssigment(symbol1, OptionalExpression.HasExpression(binaryExpr))

        // Finally println with binary expression
        val printFunction = astFactory.createPrintFunction(OptionalExpression.HasExpression(binaryExpr))

        // Test each part separately to verify violations
        val report1 = linter.analyze(declaration, configFile)
        val report2 = linter.analyze(assignment, configFile)
        val report3 = linter.analyze(printFunction, configFile)

        assertTrue(report1.hasViolations())
        assertTrue(report2.hasViolations())
        assertTrue(report3.hasViolations()) // cambiar a size check
    }

    // ========== HELPER METHODS ==========

    private fun createJsonConfig(config: Map<String, Any>): String {
        val configFile = tempDir.resolve("config.json").toFile()
        val jsonContent = config.entries.joinToString(",\n") { (key, value) ->
            "  \"$key\": ${if (value is String) "\"$value\"" else value}"
        }
        configFile.writeText("{\n$jsonContent\n}")
        return configFile.absolutePath
    }

    private fun createYamlConfig(config: Map<String, Any>): String {
        val configFile = tempDir.resolve("config.yaml").toFile()
        val yamlContent = config.entries.joinToString("\n") { (key, value) ->
            "$key: $value"
        }
        configFile.writeText(yamlContent)
        return configFile.absolutePath
    }
}
