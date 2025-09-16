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
) : ASTVisitor<ASTNode>{

    private val environment = mutableMapOf<String, Variable>()
    private val stack = mutableListOf<Any?>()


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
    }

    fun pushLiteral(value: Any?) = stack.add(value)
    fun popLiteral(): Any? = if (stack.isEmpty()) null else stack.removeAt(stack.size - 1)

    fun declareVariable(variable: Variable) {
        if (environment.containsKey(variable.name)) {
            errorHandler.handleError("Variable $variable.name already declared")
            return
        }
        environment[variable.name] = variable
    }

    fun assignVariable(name: String, value: Any?) {
        if (!environment.containsKey(name)) {
            errorHandler.handleError("Variable $name not declared")
            return
        }
        val variable = environment[name]!!
        if (variable.immutable && variable.value != null) {
            errorHandler.handleError("Immutable variable $name already assigned")
            return
        }
        environment[name] = Variable(variable.name, value, variable.immutable)
    }

    fun getEnvVar(name: String): Any? = environment[name]


    override fun visitBinary(expr: BinaryExpression): ASTNode {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)
        val result = when (expr.operator) {
            Operator.ADD -> when {
                left is Number && right is Number -> left.toDouble() + right.toDouble()
                else -> left.toString() + right.toString() // concatenación
            }
            Operator.SUB -> (left as Number).toDouble() - (right as Number).toDouble()
            Operator.MUL -> (left as Number).toDouble() * (right as Number).toDouble()
            Operator.DIV -> (left as Number).toDouble() / (right as Number).toDouble()
            Operator.MOD -> (left as Number).toDouble() % (right as Number).toDouble()
        }
        pushLiteral(result)
        return expr
    }



    override fun visitBoolean(expr: BooleanExpression): ASTNode {
        pushLiteral(expr.value.equals("true", ignoreCase = true))
        return expr
    }

    override fun visitNumber(expr: NumberExpression): ASTNode {
        val number = expr.value.toIntOrNull() ?: expr.value.toDoubleOrNull() ?: 0
        pushLiteral(number)
        return expr
    }

    override fun visitString(expr: StringExpression): ASTNode {
        pushLiteral(expr.value)
        return expr
    }

    override fun visitReadInput(expr: ReadInputExpression): ASTNode {
        val prompt = when (val opt = expr.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression) as? String ?: ""
            is OptionalExpression.NoExpression -> ""
        }

        val input = inputProvider.readInput(prompt)
        if (input == null) {
            reportError("No input provided")
            pushLiteral(null)
            return expr
        }

        val value: Any = when {
            input.equals("true", ignoreCase = true) || input.equals("false", ignoreCase = true) -> input.equals("true", ignoreCase = true)
            input.toIntOrNull() != null -> input.toInt()
            input.toDoubleOrNull() != null -> input.toDouble()
            else -> input
        }

        pushLiteral(value)
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
        val value = getEnvVar(expr.value)
        pushLiteral(value)
        return expr
    }

    override fun visitPrintFunction(statement: PrintFunction): ASTNode {
        val value = when (val opt = statement.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression)
            is OptionalExpression.NoExpression -> null
        }
        value?.let { printValue(it) }
        return statement
    }

    override fun visitCondition(statement: Condition): ASTNode {
        val cond = evaluate(statement.condition) as? Boolean ?: false
        if (cond) {
            statement.ifBlock.forEach { evaluate(it) }
        } else {
            statement.elseBlock?.forEach { evaluate(it) }
        }
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
}
