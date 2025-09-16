import org.example.ast.ASTNode
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.ReadInputExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.ErrorHandler
import org.example.common.Position
import org.example.common.PrintScriptIterator
import org.example.common.Range
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.common.results.Error
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.linter.Linter
import org.example.linter.provider.LinterVersionProvider
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertTrue

class LinterTest {

    //linter -----------------------------------------
    class TestIterator(private val items: List<ASTNode>) : PrintScriptIterator<Result> {
        private var index = 0
        override fun hasNext() = index < items.size
        override fun getNext(): Result = Success(items[index++])
    }

    val astFactory = AstFactory()

    private fun createLinter(version: String, nodes: List<ASTNode>, inputStream: InputStream, errorHandler: ErrorHandler) : Linter {
        val provider = LinterVersionProvider().with(version)
        return provider.provide(TestIterator(nodes), inputStream, errorHandler)
    }

    private fun configStream(
        printlnOnlyLiteralsAndIdentifiers: Boolean? = null,
        identifierFormat: String? = null,
        readInputOnlyLiteralsAndIdentifiers: Boolean? = null
    ): InputStream {
        val parts = mutableListOf<String>()
        if (printlnOnlyLiteralsAndIdentifiers != null) {
            parts += "\"mandatory-variable-or-literal-in-println\": $printlnOnlyLiteralsAndIdentifiers"
        }
        if (identifierFormat != null) {
            parts += "\"identifier_format\": \"$identifierFormat\""
        }
        if (readInputOnlyLiteralsAndIdentifiers != null) {
            parts += "\"mandatory-variable-or-literal-in-readInput\": $readInputOnlyLiteralsAndIdentifiers"
        }
        val json = "{${parts.joinToString(",")}}"
        return ByteArrayInputStream(json.toByteArray())
    }



    //astnodes -----------------------------------------

    // let userName: string = "Juan"
    val varDeclCamel: VariableDeclarator =
        astFactory.createVariableDeclarator(
            astFactory.createSymbol("userName"),
            Type.STRING,
            OptionalExpression.HasExpression(astFactory.createString("Juan"))
        )

    // let user_name: string = "Juan"
    val varDeclSnake: VariableDeclarator =
        astFactory.createVariableDeclarator(
            astFactory.createSymbol("user_name"),
            Type.STRING,
            OptionalExpression.HasExpression(astFactory.createString("Juan"))
        )

    // userName = 1
    val assignCamel: VariableAssigner =
        astFactory.createVariableAssigment(
            astFactory.createSymbol("userName"),
            OptionalExpression.HasExpression(astFactory.createNumber("1"))
        )

    // println("hola")
    val printLiteral: PrintFunction =
        astFactory.createPrintFunction(
            OptionalExpression.HasExpression(astFactory.createString("hola"))
        )

    // println(1 + 2)
    val binLiteralPlus: org.example.ast.expressions.Expression =
        astFactory.createBinaryExpression(
            astFactory.createNumber("1"),
            Operator.ADD,
            astFactory.createNumber("2")
        )
    val printBinary: PrintFunction =
        astFactory.createPrintFunction(
            OptionalExpression.HasExpression(binLiteralPlus)
        )

    // println(userAge + 1)
    val binIdPlusNum: org.example.ast.expressions.Expression =
        astFactory.createBinaryExpression(
            astFactory.createSymbol("userAge"),
            Operator.ADD,
            astFactory.createNumber("1")
        )
    val printBinaryWithIdentifier: PrintFunction =
        astFactory.createPrintFunction(
            OptionalExpression.HasExpression(binIdPlusNum)
        )

    val readInputIdentifier = ReadInputExpression(
        OptionalExpression.HasExpression(astFactory.createSymbol("promptVar")),
        astFactory.basicRange
    )


    val bin = astFactory.createBinaryExpression(
            astFactory.createNumber("1"),
            Operator.ADD,
            astFactory.createNumber("2")
        )
    val readInputBinary =ReadInputExpression(
            OptionalExpression.HasExpression(bin),
            astFactory.basicRange
        )

// tests -------------------------------------------------

        @Test
        fun `regla habilitada - permite literal e identificador, prohíbe binaria`() {
            val nodes = listOf(printLiteral, printBinary, printBinaryWithIdentifier)
            val fakeErrorHandler = object : ErrorHandler {
                val errors = mutableListOf<String>()
                override fun handleError(message: String) {
                    errors.add(message)
                }
            }
            val linter =
                createLinter("1.0", nodes, configStream(printlnOnlyLiteralsAndIdentifiers = true), fakeErrorHandler)

            // Casos OK
            val r1 = linter.getNext()
            assertTrue(r1 is Success<*>)
            assertTrue(fakeErrorHandler.errors.isEmpty())

            // Casos prohibidos
            val r3 = linter.getNext()
            assertTrue(linter.hasNext())
            val r4 = linter.getNext()
            assertTrue(r3 is Success<*>)
            assertTrue(r4 is Success<*>)
            assertTrue(
                fakeErrorHandler.errors.isNotEmpty()
            )
        }

        @Test
        fun `regla deshabilitada - no reporta nada aunque el argumento sea binario`() {
            val nodes = listOf(printBinary)
            val fakeErrorHandler = object : ErrorHandler {
                val errors = mutableListOf<String>()
                override fun handleError(message: String) {
                    errors.add(message)
                }
            }
            val linter =
                createLinter("1.0", nodes, configStream(printlnOnlyLiteralsAndIdentifiers = false), fakeErrorHandler)

            val r = linter.getNext()
            assertTrue(r is Success<*>)
            assertTrue(
                fakeErrorHandler.errors.isEmpty(),
            )
        }



        @Test
        fun `identifier_format=camelCase - camel OK, snake reporta error`() {
            val nodes = listOf(varDeclCamel, assignCamel, varDeclSnake)
            val fakeErrorHandler = object : ErrorHandler {
                val errors = mutableListOf<String>()
                override fun handleError(message: String) {
                    errors.add(message)
                }
            }

            val linter = createLinter(
                "1.0",
                nodes,
                configStream(identifierFormat = "camelCase"),
                fakeErrorHandler
            )

            val r1 = linter.getNext()
            val r2 = linter.getNext()
            assertTrue(r1 is Success<*>)
            assertTrue(r2 is Success<*>)
            assertTrue(fakeErrorHandler.errors.isEmpty())

            val r3 = linter.getNext()
            assertTrue(r3 is Success<*>)
            assertTrue(fakeErrorHandler.errors.isNotEmpty())
        }

        @Test
        fun `identifier_format=snake_case - snake OK, camel reporta error`() {
            val nodes = listOf(varDeclSnake, varDeclCamel)
            val fakeErrorHandler = object : ErrorHandler {
                val errors = mutableListOf<String>()
                override fun handleError(message: String) {
                    errors.add(message)
                }
            }

            val linter = createLinter(
                "1.0",
                nodes,
                configStream(identifierFormat = "snake_case"),
                fakeErrorHandler
            )

            // OK (snake)
            val r1 = linter.getNext()
            assertTrue(r1 is Success<*>)
            assertTrue(fakeErrorHandler.errors.isEmpty())

            // Camel => debería reportar
            val r2 = linter.getNext()
            assertTrue(r2 is Success<*>)
            assertTrue(fakeErrorHandler.errors.isNotEmpty())
        }

        @Test
        fun `identifier_format no seteado - no reporta nada (regla deshabilitada)`() {
            val nodes = listOf(varDeclCamel, varDeclSnake)
            val fakeErrorHandler = object : ErrorHandler {
                val errors = mutableListOf<String>()
                override fun handleError(message: String) {
                    errors.add(message)
                }
            }

            val linter = createLinter(
                "1.0",
                nodes,
                configStream(printlnOnlyLiteralsAndIdentifiers = true),
                fakeErrorHandler
            )

            val r1 = linter.getNext()
            val r2 = linter.getNext()
            assertTrue(r1 is Success<*>)
            assertTrue(r2 is Success<*>)
            assertTrue(fakeErrorHandler.errors.isEmpty())
        }

        @Test
        fun `regla habilitada - print con boolean como argumento reporta error`() {
            // println(true)
            val booleanArg = BooleanExpression("true", astFactory.basicPosition)
            val printBooleanFail: PrintFunction =
                astFactory.createPrintFunction(
                    OptionalExpression.HasExpression(booleanArg)
                )

            val fakeErrorHandler = object : ErrorHandler {
                val errors = mutableListOf<String>()
                override fun handleError(message: String) {
                    errors.add(message)
                }
            }

            val linter = createLinter(
                "1.0",
                listOf(printBooleanFail, booleanArg),
                configStream(printlnOnlyLiteralsAndIdentifiers = true),
                fakeErrorHandler
            )

            val r = linter.getNext()
            assertTrue(r is Error)
            val r2 = linter.getNext()
            assertTrue(r2 is Error)
            assertTrue(fakeErrorHandler.errors.isEmpty())
        }


        //tests for linter 1.1 -------------------------
        @Test
        fun `regla habilitada - print con boolean como argumento no reporta error`() {
            // println(true)
            val booleanArg = BooleanExpression("true", astFactory.basicPosition)
            val printBooleanFail: PrintFunction =
                astFactory.createPrintFunction(
                    OptionalExpression.HasExpression(booleanArg)
                )

            val fakeErrorHandler = object : ErrorHandler {
                val errors = mutableListOf<String>()
                override fun handleError(message: String) {
                    errors.add(message)
                }
            }

            val linter = createLinter(
                "1.1",
                listOf(printBooleanFail, booleanArg),
                configStream(printlnOnlyLiteralsAndIdentifiers = true),
                fakeErrorHandler
            )

            val r = linter.getNext()
            assertTrue(r is Success<*>)
            val r2 = linter.getNext()
            assertTrue(r2 is Success<*>)
            assertTrue(fakeErrorHandler.errors.isEmpty())
        }

        @Test
        fun `readInput habilitada - permite literal e identificador, prohíbe binaria`() {
            val nodes = listOf(readInputIdentifier, readInputBinary)
            val fakeErrorHandler = object : ErrorHandler {
                val errors = mutableListOf<String>()
                override fun handleError(message: String) {
                    errors.add(message)
                }
            }

            val linter = createLinter(
                "1.1",
                nodes,
                configStream(readInputOnlyLiteralsAndIdentifiers = true),
                fakeErrorHandler
            )

            // Casos OK
            val r1 = linter.getNext()
            assertTrue(r1 is Success<*>)
            assertTrue(fakeErrorHandler.errors.isEmpty())

            // Caso prohibido
            val r2 = linter.getNext()
            assertTrue(r2 is Success<*>)
            assertTrue(fakeErrorHandler.errors.isNotEmpty())
        }

    private fun readInputBinaryWithRange(range: Range): ReadInputExpression {
        val bin = astFactory.createBinaryExpression(
            astFactory.createNumber("1"),
            Operator.ADD,
            astFactory.createNumber("2")
        )
        return ReadInputExpression(
            OptionalExpression.HasExpression(bin),
            range
        )
    }

    @Test
    fun `readInput habilitada - mensaje incluye nombre de funcion y range`() {
        // rango distintivo para asertar exactamente
        val customRange = Range(Position(7, 4), Position(7, 12))
        val node = readInputBinaryWithRange(customRange)

        val fakeErrorHandler = object : ErrorHandler {
            val errors = mutableListOf<String>()
            override fun handleError(message: String) { errors.add(message) }
        }

        // Usar provider 1.1
        val linter = createLinter(
            "1.1",
            listOf(node),
            configStream(readInputOnlyLiteralsAndIdentifiers = true),
            fakeErrorHandler
        )

        val r = linter.getNext()
        assertTrue(r is Success<*>)

        assertTrue(fakeErrorHandler.errors.isNotEmpty())
        val msg = fakeErrorHandler.errors.first()

        assertTrue("readInput()" in msg)
        assertTrue(customRange.toString() in msg, "El mensaje debe incluir el range del ReadInputExpression")
    }

    @Test
    fun `readInput habilitada - literal permitido no reporta`() {
        val fakeErrorHandler = object : ErrorHandler {
            val errors = mutableListOf<String>()
            override fun handleError(message: String) { errors.add(message) }
        }

        val linter = createLinter(
            "1.1",
            listOf(readInputIdentifier),
            configStream(readInputOnlyLiteralsAndIdentifiers = true),
            fakeErrorHandler
        )

        val r1 = linter.getNext()
        assertTrue(r1 is Success<*>)
        assertTrue(fakeErrorHandler.errors.isEmpty(), "Literal o identificador no deberían reportar")
    }

    @Test
    fun `1_1 camelCase - marca snake_case en todos los nodos soportados`() {
        val snake = "bad_name"

        val nodes = listOf(
            // SymbolExpression directo
            astFactory.createSymbol(snake),
            // BinaryExpression: símbolo a la izquierda (y número a la derecha)
            astFactory.createBinaryExpression(astFactory.createSymbol(snake), Operator.ADD, astFactory.createNumber("1")),
            // PrintFunction(value = SymbolExpression snake)
            astFactory.createPrintFunction(OptionalExpression.HasExpression(astFactory.createSymbol(snake))),
            // VariableAssigner(symbol = snake, value = número)
            astFactory.createVariableAssigment(astFactory.createSymbol(snake), OptionalExpression.HasExpression(astFactory.createNumber("10"))),
            // VariableDeclarator(symbol = snake, value = string)
            astFactory.createVariableDeclarator(astFactory.createSymbol(snake), Type.STRING, OptionalExpression.HasExpression(astFactory.createString("x"))),
            // ReadInputExpression(value = SymbolExpression snake)
            ReadInputExpression(OptionalExpression.HasExpression(astFactory.createSymbol(snake)), astFactory.basicRange),
        )

        val eh = object : ErrorHandler {
            val errors = mutableListOf<String>()
            override fun handleError(message: String) { errors += message }
        }

        val linter = createLinter(
            "1.1",
            nodes,
            configStream(identifierFormat = "camelCase"),
            eh
        )

        // corremos todos los nodos
        while (linter.hasNext()) {
            val r = linter.getNext()
            assertTrue(r is Success<*>)
        }

        // Cada nodo de arriba contiene exactamente 1 símbolo snake a chequear -> 6 errores
        kotlin.test.assertEquals(6, eh.errors.size, "Debe reportar formato inválido para cada símbolo snake_case")
    }

    @Test
    fun `1_1 camelCase - NO marca camelCase en todos los nodos soportados`() {
        val camel = "goodName"

        val nodes = listOf(
            astFactory.createSymbol(camel),
            astFactory.createBinaryExpression(astFactory.createSymbol(camel), Operator.ADD, astFactory.createNumber("1")),
            astFactory.createPrintFunction(OptionalExpression.HasExpression(astFactory.createSymbol(camel))),
            astFactory.createVariableAssigment(astFactory.createSymbol(camel), OptionalExpression.HasExpression(astFactory.createNumber("10"))),
            astFactory.createVariableDeclarator(astFactory.createSymbol(camel), Type.STRING, OptionalExpression.HasExpression(astFactory.createString("x"))),
            ReadInputExpression(OptionalExpression.HasExpression(astFactory.createSymbol(camel)), astFactory.basicRange),
        )

        val eh = object : ErrorHandler {
            val errors = mutableListOf<String>()
            override fun handleError(message: String) { errors += message }
        }

        val linter = createLinter(
            "1.1",
            nodes,
            configStream(identifierFormat = "camelCase"),
            eh
        )

        while (linter.hasNext()) {
            val r = linter.getNext()
            assertTrue(r is Success<*>)
        }
        assertTrue(eh.errors.isEmpty(), "No deberían reportarse errores para camelCase válido")
    }

    @Test
    fun `1_1 snake_case - marca camelCase en todos los nodos soportados`() {
        val camel = "badName"

        val nodes = listOf(
            astFactory.createSymbol(camel),
            astFactory.createBinaryExpression(astFactory.createSymbol(camel), Operator.ADD, astFactory.createNumber("1")),
            astFactory.createPrintFunction(OptionalExpression.HasExpression(astFactory.createSymbol(camel))),
            astFactory.createVariableAssigment(astFactory.createSymbol(camel), OptionalExpression.HasExpression(astFactory.createNumber("10"))),
            astFactory.createVariableDeclarator(astFactory.createSymbol(camel), Type.STRING, OptionalExpression.HasExpression(astFactory.createString("x"))),
            ReadInputExpression(OptionalExpression.HasExpression(astFactory.createSymbol(camel)), astFactory.basicRange),
        )

        val eh = object : ErrorHandler {
            val errors = mutableListOf<String>()
            override fun handleError(message: String) { errors += message }
        }

        val linter = createLinter(
            "1.1",
            nodes,
            configStream(identifierFormat = "snake_case"),
            eh
        )

        while (linter.hasNext()) {
            val r = linter.getNext()
            assertTrue(r is Success<*>)
        }
        kotlin.test.assertEquals(6, eh.errors.size, "Debe reportar cada símbolo camelCase bajo regla snake_case")
    }

    @Test
    fun `1_1 camelCase - BinaryExpression con dos símbolos snake produce dos errores (recorre left y right)`() {
        val leftSnake  = astFactory.createSymbol("left_bad_name")
        val rightSnake = astFactory.createSymbol("right_bad_name")
        val binBoth = astFactory.createBinaryExpression(leftSnake, Operator.ADD, rightSnake)

        val eh = object : ErrorHandler {
            val errors = mutableListOf<String>()
            override fun handleError(message: String) { errors += message }
        }

        val linter = createLinter(
            "1.1",
            listOf(binBoth),
            configStream(identifierFormat = "camelCase"),
            eh
        )

        val r = linter.getNext()
        assertTrue(r is Success<*>)
        kotlin.test.assertEquals(2, eh.errors.size, "Debe reportar por cada símbolo snake en left y right")
    }

    @Test
    fun `1_1 camelCase - ignora hojas no-símbolo (Number, Boolean, String) y no reporta`() {
        // Estos nodos no contienen símbolos; el handler no debería reportar nada
        val nodes = listOf(
            // BooleanExpression, NumberExpression y StringExpression se ignoran en el when del handler
            BooleanExpression("true", astFactory.basicPosition),
            astFactory.createNumber("123"),
            astFactory.createString("hola")
        )

        val eh = object : ErrorHandler {
            val errors = mutableListOf<String>()
            override fun handleError(message: String) { errors += message }
        }

        val linter = createLinter(
            "1.1",
            nodes,
            configStream(identifierFormat = "camelCase"),
            eh
        )

        while (linter.hasNext()) {
            val r = linter.getNext()
            assertTrue(r is Success<*>)
        }
        assertTrue(eh.errors.isEmpty(), "Nodos hoja sin símbolos no deberían reportar")
    }

    }