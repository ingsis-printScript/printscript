package org.example.integration

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.Expression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.common.results.Error
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.parser.Parser
import org.example.parser.TokenBuffer
import org.example.parser.provider.ParserProvider10
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Test de integracion Lexer → Parser
 * Unificado al layout/metodología del ParserTest (helpers, parseAll, assertParsed/assertInvalid)
 * pero usando Lexer real (no tokens mockeados).
 */
class ValidationTest {

    private lateinit var utils: PrintScriptTestUtils
    private lateinit var astFactory: AstFactory
    private val provider = ParserProvider10()

    @BeforeEach
    fun setup() {
        utils = PrintScriptTestUtils()
        astFactory = AstFactory()
    }

    // -----------------------------------------
    // Helpers (parser)
    // -----------------------------------------

    private fun parserFromSource(source: String): Parser {
        val lexer = utils.createLexer(source)
        val tokenBuffer = TokenBuffer(lexer)
        return provider.provide(tokenBuffer)
    }

    /** Parsea TODO el programa, acumulando nodos, fallando ante el primer Error. */
    private fun parseAllFromSource(source: String): List<ASTNode> {
        val parser = parserFromSource(source)
        val nodes = mutableListOf<ASTNode>()
        while (parser.hasNext()) {
            val result = parser.parse()
            require(result is Success<*>) { "Se esperaba Success pero fue $result" }
            nodes.add(result.value as ASTNode)
        }
        return nodes
    }

    // -----------------------------------------
    // Helpers (AST builders)
    // -----------------------------------------

    private fun v(name: String) = astFactory.createSymbol(name)
    private fun n(num: String) = astFactory.createNumber(num)
    private fun s(str: String) = astFactory.createString(str)

    private fun decl(
        name: String,
        type: Type,
        value: OptionalExpression = OptionalExpression.NoExpression
    ) = astFactory.createVariableDeclarator(v(name), type, value)

    private fun assign(name: String, value: OptionalExpression) =
        astFactory.createVariableAssigment(v(name), value)

    private fun bin(left: Expression, op: Operator, right: Expression) =
        astFactory.createBinaryExpression(left, op, right)

    private fun print(value: OptionalExpression) =
        astFactory.createPrintFunction(value)

    // -----------------------------------------
    // Matchers: igualdad profunda ignorando position/range
    // -----------------------------------------

    private fun assertAstEqualsIgnoringPos(expected: ASTNode, actual: ASTNode) {
        when {
            expected is VariableDeclarator && actual is VariableDeclarator -> {
                assertSymbolEq(expected.symbol, actual.symbol)
                assertEquals(expected.type, actual.type, "Tipo de declaración")
                assertOptEq(expected.value, actual.value)
            }
            expected is VariableAssigner && actual is VariableAssigner -> {
                assertSymbolEq(expected.symbol, actual.symbol)
                assertOptEq(expected.value, actual.value)
            }
            expected is PrintFunction && actual is PrintFunction -> {
                assertOptEq(expected.value, actual.value)
            }
            expected is Expression && actual is Expression -> {
                assertExprEq(expected, actual)
            }
            else -> fail("Tipos no coinciden o no soportados: ${expected::class} vs ${actual::class}")
        }
    }

    private fun assertSymbolEq(e: SymbolExpression, a: SymbolExpression) {
        assertEquals(e.value, a.value, "Symbol.value")
    }

    private fun assertOptEq(e: OptionalExpression, a: OptionalExpression) {
        when {
            e is OptionalExpression.NoExpression && a is OptionalExpression.NoExpression -> return
            e is OptionalExpression.HasExpression && a is OptionalExpression.HasExpression ->
                assertExprEq(e.expression, a.expression)
            else -> fail("OptionalExpression no coincide: $e vs $a")
        }
    }

    private fun assertExprEq(e: Expression, a: Expression) {
        when {
            e is SymbolExpression && a is SymbolExpression ->
                assertEquals(e.value, a.value, "Symbol.value")
            e is NumberExpression && a is NumberExpression ->
                assertEquals(e.value, a.value, "Number.value")
            e is StringExpression && a is StringExpression ->
                assertEquals(e.value, a.value, "String.value")
            e is BinaryExpression && a is BinaryExpression -> {
                assertEquals(e.operator, a.operator, "Binary.operator")
                assertExprEq(e.left, a.left)
                assertExprEq(e.right, a.right)
            }
            else -> fail("Expression no soportada: ${e::class} vs ${a::class}")
        }
    }

    // -----------------------------------------
    // Helpers (wrappers de asserts existentes)
    // -----------------------------------------

    /** Parsea un único statement y compara contra el AST esperado ignorando posiciones. */
    private inline fun <reified T> assertParsedSource(source: String, expected: T) {
        val parser = parserFromSource(source)
        val result = parser.parse()
        assertTrue(result is Success<*>, "Se esperaba Success pero fue $result")
        @Suppress("UNCHECKED_CAST")
        assertAstEqualsIgnoringPos(expected as ASTNode, (result as Success<*>).value as ASTNode)
        assertFalse(parser.hasNext(), "No deberían quedar mas statements")
    }

    /** Parsea y valida que devuelva Error con prefijo coherente. */
    private fun assertInvalidSource(source: String, messagePrefix: String = "Error in statement: ") {
        val parser = parserFromSource(source)
        val result: Result = parser.parse()
        assertTrue(result is Error, "Se esperaba Error pero fue $result")
        assertTrue(
            (result as Error).message.startsWith(messagePrefix),
            "El mensaje debe comenzar con '$messagePrefix' pero fue '${result.message}'"
        )
    }

    // -----------------------------------------
    // Helpers (posiciones calculadas desde el source)
    // -----------------------------------------

    private fun firstLine(source: String): String =
        source.lineSequence().first().trimEnd('\r')

    private fun colOf(line: String, needle: String, from: Int = 0): Int {
        val idx = line.indexOf(needle, from)
        require(idx >= 0) { "No se encontró '$needle' en la línea: '$line'" }
        return idx
    }

    private fun endCol(line: String): Int = line.lastIndex

    // ========================================
    // CASOS BÁSICOS (una sola sentencia)
    // ========================================

    @Test
    fun `empty source devuelve Error No tokens`() {
        val parser = parserFromSource("")
        assertEquals(Error("No tokens to parse"), parser.parse())
    }

    @Test
    fun `declaracion simple sin asignacion`() {
        val expected = decl("x", Type.NUMBER)
        assertParsedSource("let x: Number;", expected)
    }

    @Test
    fun `declaracion con asignacion aritmetica simple`() {
        val expr = bin(n("5"), Operator.ADD, n("3"))
        val expected = decl("x", Type.NUMBER, OptionalExpression.HasExpression(expr))
        assertParsedSource("let x: Number = 5 + 3;", expected)
    }

    @Test
    fun `declaracion con string literal`() {
        val expected = decl("x", Type.STRING, OptionalExpression.HasExpression(s("'John'")))
        assertParsedSource("""let x: String = 'John';""", expected)
    }

    @Test
    fun `asignacion numerica simple`() {
        val expected = assign("x", OptionalExpression.HasExpression(n("10")))
        assertParsedSource("x = 10;", expected)
    }

    @Test
    fun `asignacion con otra variable`() {
        val expected = assign("y", OptionalExpression.HasExpression(v("x")))
        assertParsedSource("y = x;", expected)
    }

    @Test
    fun `println con parametro string`() {
        val expected = print(OptionalExpression.HasExpression(s("'Hello'")))
        assertParsedSource("""println('Hello');""", expected)
    }

    // ========================================
    // MULTI-STATEMENT (estilo TCK)
    // ========================================

    @Test
    fun `secuencia declare-assign-print (string)`() {
        val src = """
            let x: Number = 5;
            y = x;
            println('done');
        """.trimIndent()

        val nodes = parseAllFromSource(src)
        assertEquals(3, nodes.size)

        val expected0 = decl("x", Type.NUMBER, OptionalExpression.HasExpression(n("5")))
        val expected1 = assign("y", OptionalExpression.HasExpression(v("x")))
        val expected2 = print(OptionalExpression.HasExpression(s("'done'")))

        assertAstEqualsIgnoringPos(expected0, nodes[0])
        assertAstEqualsIgnoringPos(expected1, nodes[1])
        assertAstEqualsIgnoringPos(expected2, nodes[2])
    }

    @Test
    fun `operaciones aritmeticas y print`() {
        // let numberResult: Number = 5 * 5 - 8; println(numberResult);
        val src = """
            let numberResult: Number = 5 * 5 - 8;
            println(numberResult);
        """.trimIndent()

        val nodes = parseAllFromSource(src)
        assertEquals(2, nodes.size)

        val mul = bin(n("5"), Operator.MUL, n("5"))
        val expr = bin(mul, Operator.SUB, n("8"))
        val expectedDecl = decl("numberResult", Type.NUMBER, OptionalExpression.HasExpression(expr))
        val expectedPrint = print(OptionalExpression.HasExpression(v("numberResult")))

        assertAstEqualsIgnoringPos(expectedDecl, nodes[0])
        assertAstEqualsIgnoringPos(expectedPrint, nodes[1])
    }

    @Test
    fun `concat string + number y print`() {
        // let someNumber: Number = 1; let someString: String = "hello world "; println(someString + someNumber);
        val src = """
            let someNumber: Number = 1;
            let someString: String = 'hello world ';
            println(someString + someNumber);
        """.trimIndent()

        val nodes = parseAllFromSource(src)
        assertEquals(3, nodes.size)

        val expected0 = decl("someNumber", Type.NUMBER, OptionalExpression.HasExpression(n("1")))
        val expected1 = decl("someString", Type.STRING, OptionalExpression.HasExpression(s("'hello world '")))
        val concat = bin(v("someString"), Operator.ADD, v("someNumber"))
        val expected2 = print(OptionalExpression.HasExpression(concat))

        assertAstEqualsIgnoringPos(expected0, nodes[0])
        assertAstEqualsIgnoringPos(expected1, nodes[1])
        assertAstEqualsIgnoringPos(expected2, nodes[2])
    }

    @Test
    fun `aritmetica con decimales y print`() {
        // let pi: Number; pi = 3.14; println(pi / 2);
        val src = """
            let pi: Number;
            pi = 3.14;
            println(pi / 2);
        """.trimIndent()

        val nodes = parseAllFromSource(src)
        assertEquals(3, nodes.size)

        val expectedDecl = decl("pi", Type.NUMBER)
        val expectedAssign = assign("pi", OptionalExpression.HasExpression(n("3.14")))
        val expectedPrint = print(
            OptionalExpression.HasExpression(
                bin(v("pi"), Operator.DIV, n("2"))
            )
        )

        assertAstEqualsIgnoringPos(expectedDecl, nodes[0])
        assertAstEqualsIgnoringPos(expectedAssign, nodes[1])
        assertAstEqualsIgnoringPos(expectedPrint, nodes[2])
    }

    // ========================================
    // ERRORES
    // ========================================

    @Test
    fun `println sin parametros es invalido`() {
        assertInvalidSource("println();")
    }

    @Test
    fun `syntax invalida - falta identificador en let`() {
        assertInvalidSource("let = 5;")
    }

    @Test
    fun `solo punto y coma`() {
        assertInvalidSource(";")
    }

    // ========================================
    // POSICIONES (3 tests con cálculo desde source)
    // ========================================

    @Test
    fun `posiciones - declaracion con 5 + 3`() {
        val src = "let x: Number = 5 + 3;"
        val parser = parserFromSource(src)
        val decl = (parser.parse() as Success<*>).value as VariableDeclarator
        val line = firstLine(src)

        val colX = colOf(line, "x")
        val colEq = colOf(line, "=")
        val col5 = colOf(line, "5", from = colEq)
        val colPlus = colOf(line, "+", from = col5) // no lo usamos, pero sirve como doc
        val col3 = colOf(line, "3", from = colPlus)
        val start = 0
        val end = endCol(line)

        assertEquals(1, decl.symbol.position.line)
        assertEquals(colX, decl.symbol.position.column)

        val has = decl.value as OptionalExpression.HasExpression
        val bin = has.expression as BinaryExpression
        val leftNum = bin.left as NumberExpression
        val rightNum = bin.right as NumberExpression

        assertions(leftNum, col5, rightNum, col3, start, decl, end)
    }

    private fun assertions(
        leftNum: NumberExpression,
        col5: Int,
        rightNum: NumberExpression,
        col3: Int,
        start: Int,
        decl: VariableDeclarator,
        end: Int
    ) {
        assertEquals(1, leftNum.position.line)
        assertEquals(col5, leftNum.position.column)

        assertEquals(1, rightNum.position.line)
        assertEquals(col3, rightNum.position.column)

        assertEquals(start, decl.range.start.column)
        assertEquals(end, decl.range.end.column)
    }

    @Test
    fun `posiciones - asignacion decimal 3_14`() {
        val src = "pi = 3.14;"
        val parser = parserFromSource(src)
        val assign = (parser.parse() as Success<*>).value as VariableAssigner
        val line = firstLine(src)

        val colPi = colOf(line, "pi")
        val colEq = colOf(line, "=")
        val col314 = colOf(line, "3.14", from = colEq)
        val end = endCol(line)

        assertEquals(1, assign.symbol.position.line)
        assertEquals(colPi, assign.symbol.position.column)

        val valueExpr = (assign.value as OptionalExpression.HasExpression).expression as NumberExpression
        assertEquals(1, valueExpr.position.line)
        assertEquals(col314, valueExpr.position.column)

        assertEquals(0, assign.range.start.column)
        assertEquals(end, assign.range.end.column)
    }

    @Test
    fun `posiciones - println con suma de simbolos`() {
        val src = "println(someString + someNumber);"
        val parser = parserFromSource(src)
        val printFn = (parser.parse() as Success<*>).value as PrintFunction
        val line = firstLine(src)

        val colLparen = colOf(line, "(")
        val colSomeStr = colOf(line, "someString", from = colLparen)
        val colPlus = colOf(line, "+", from = colSomeStr)
        val colSomeNum = colOf(line, "someNumber", from = colPlus)
        val end = endCol(line)

        val expr = (printFn.value as OptionalExpression.HasExpression).expression as BinaryExpression
        val leftSym = expr.left as SymbolExpression
        val rightSym = expr.right as SymbolExpression

        assertEquals(colSomeStr, leftSym.position.column)
        assertEquals(colSomeNum, rightSym.position.column)

        assertEquals(0, printFn.range.start.column)
        assertEquals(end, printFn.range.end.column)
    }
}
