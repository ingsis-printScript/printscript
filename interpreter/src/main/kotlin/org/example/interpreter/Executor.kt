package org.example.interpreter

import org.example.ast.ASTNode
import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.ReadEnvExpression
import org.example.ast.expressions.ReadInputExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.Condition
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.ast.visitor.ASTVisitor
import org.example.common.ErrorHandler
import org.example.common.enums.Operator
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.OutputPrinter

class Executor(
    val inputProvider: InputProvider,
    val outputPrinter: OutputPrinter,
    val errorHandler: ErrorHandler
) : ASTVisitor<ASTNode> {

    private val environment = mutableMapOf<String, Variable>()
    private val stack = mutableListOf<Any?>()
    private var hasExecutionError = false

    fun evaluate(node: ASTNode): Any? {
        node.accept(this)
        return popLiteral()
    }

    fun lookupVariable(name: String): Any? = environment[name]

    fun printValue(value: Any) {
        val s = value.toString()
        val unquoted = if (s.length >= 2 && s.first() == '"' && s.last() == '"') {
            s.substring(1, s.length - 1)
        } else {
            s
        }
        outputPrinter.print(unquoted)
    }

    fun reportError(message: String) {
        errorHandler.handleError(message)
        hasExecutionError = true
    }

    fun pushLiteral(value: Any?) = stack.add(value)
    fun popLiteral(): Any? = if (stack.isEmpty()) null else stack.removeAt(stack.size - 1)

    fun declareVariable(variable: Variable) {
        if (environment.containsKey(variable.name)) {
            reportError("Variable $variable.name already declared")
            return
        }
        environment[variable.name] = variable
    }

    fun assignVariable(name: String, value: Any?) {
        if (!environment.containsKey(name)) {
            reportError("Variable $name not declared")
            return
        }
        val variable = environment[name]!!
        if (variable.immutable && variable.value != null) {
            reportError("Immutable variable $name already assigned")
            return
        }
        environment[name] = Variable(variable.name, value, variable.immutable)
    }

    fun getEnvVar(name: String): Variable? = environment[name]

    override fun visitBinary(expr: BinaryExpression): ASTNode {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)
        val result = when (expr.operator) {
            Operator.ADD -> when {
                left is Number && right is Number -> left.toDouble() + right.toDouble()
                else -> left.toString() + right.toString()
            }
            Operator.SUB -> (left as Number).toDouble() - (right as Number).toDouble()
            Operator.MUL -> (left as Number).toDouble() * (right as Number).toDouble()
            Operator.DIV -> (left as Number).toDouble() / (right as Number).toDouble()
            Operator.MOD -> (left as Number).toDouble() % (right as Number).toDouble()
        }
        if (result is Double && result % 1 == 0.0) {
            pushLiteral(result.toInt())
        } else {
            pushLiteral(result)
        }
        return expr
    }

    override fun visitBoolean(expr: BooleanExpression): ASTNode {
        pushLiteral(expr.value.equals("true", ignoreCase = true))
        return expr
    }

    override fun visitNumber(expr: NumberExpression): ASTNode {
        val intVal = expr.value.toIntOrNull()
        val number: Any = if (intVal != null) {
            intVal
        } else {
            expr.value.toDoubleOrNull() ?: 0
        }
        pushLiteral(number)
        return expr
    }

    override fun visitString(expr: StringExpression): ASTNode {
        val raw = expr.value
        val unquoted = if (raw.length >= 2 && raw.first() == '"' && raw.last() == '"') {
            raw.substring(1, raw.length - 1)
        } else {
            raw
        }
        pushLiteral(unquoted)
        return expr
    }

    override fun visitReadInput(expr: ReadInputExpression): ASTNode {
        val prompt = when (val opt = expr.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression) as? String ?: ""
            is OptionalExpression.NoExpression -> ""
        }

        outputPrinter.print(prompt)
        val input = inputProvider.readInput()

        val result: Any = when {
            input.equals("true", ignoreCase = true) -> true
            input.equals("false", ignoreCase = true) -> false
            input.toIntOrNull() != null -> input.toInt()
            input.toDoubleOrNull() != null -> input.toDouble()
            else -> input
        }

        pushLiteral(result)
        return expr
    }

    override fun visitReadEnv(expr: ReadEnvExpression): ASTNode {
        val varName = when (val opt = expr.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression) as? String ?: ""
            is OptionalExpression.NoExpression -> ""
        }

        val value = System.getenv(varName)
        if (value == null) {
            reportError("Environment variable '$varName' not found")
            pushLiteral(null)
        } else {
            pushLiteral(value)
        }
        return expr
    }

    override fun visitSymbol(expr: SymbolExpression): ASTNode {
        val variable = getEnvVar(expr.value)
        if (variable == null) {
            reportError("Undefined symbol: ${expr.value}")
            pushLiteral(null)
        } else {
            pushLiteral(variable.value)
        }
        return expr
    }

    override fun visitPrintFunction(statement: PrintFunction): ASTNode {
        if (hasExecutionError) return statement
        val value = when (val opt = statement.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression)
            is OptionalExpression.NoExpression -> null
        }
        value?.let { printValue(it) }
        return statement
    }

    override fun visitVariableAssigner(statement: VariableAssigner): ASTNode {
        val value = when (val opt = statement.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression)
            is OptionalExpression.NoExpression -> null
        }
        assignVariable(statement.symbol.value, value)
        return statement
    }

    override fun visitVariableDeclarator(statement: VariableDeclarator): ASTNode {
        val value = when (val opt = statement.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression)
            is OptionalExpression.NoExpression -> null
        }
        declareVariable(Variable(statement.symbol.value, value, immutable = false))
        return statement
    }

    override fun visitVariableImmutableDeclarator(statement: VariableImmutableDeclarator): ASTNode {
        val value = when (val opt = statement.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression)
            is OptionalExpression.NoExpression -> null
        }
        declareVariable(Variable(statement.symbol.value, value, immutable = true))
        return statement
    }

    override fun visitCondition(statement: Condition): ASTNode {
        val conditionResult = when (val opt = statement.condition) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression)
            is OptionalExpression.NoExpression -> false
        }

        val cond = (conditionResult as? Boolean) ?: false

        val blockToExecute = if (cond) statement.ifBlock else statement.elseBlock.orEmpty()

        for (stmt in blockToExecute) {
            if (hasExecutionError) break
            stmt.accept(this)
        }
        return statement
    }

}
