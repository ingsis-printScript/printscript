package org.example.interpreter

import org.example.common.ast.Program
import org.example.common.ast.expressions.BinaryExpression
import org.example.common.ast.expressions.Expression
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.expressions.LiteralExpression
import org.example.common.ast.statements.VariableDeclarator
import org.example.common.ast.statements.VariableAssigner
import org.example.common.ast.statements.FunctionCall
import org.example.common.enums.Operator
import org.example.interpreter.result.NoResult
import org.example.interpreter.result.Results
import org.example.interpreter.result.Success
import org.example.interpreter.result.Error
import org.example.interpreter.visitors.ASTVisitor

class Interpreter : ASTVisitor<Results>{

    //Para almacenar variables: (no se si la estructura de datos es la conveniente)
    private val symbolTable = mutableMapOf<String, Any?>()


    private fun visitStatement(statement: Any): Results {
        return when (statement) {
            is VariableDeclarator -> visitVariableDeclarator(statement)
            is VariableAssigner -> visitVariableAssigner(statement)
            is FunctionCall -> visitFunctionCall(statement)
            else -> Error("Unknown statement type: ${statement::class.simpleName}")
        }
    }

    override fun visitVariableDeclarator(node: VariableDeclarator): Results {
        return try {
            val value = node.value?.let {
                val result = visitExpression(it)
                if (result is Error) return result
                (result as? Success<*>)?.value
            }

            symbolTable[node.identifier.name] = value
            Success(value)
        } catch (e: Exception) {
            Error("Error declaring variable '${node.identifier.name}': ${e.message}")
        }
    }

    override fun visitVariableAssigner(node: VariableAssigner): Results {
        return try {
            if (!symbolTable.containsKey(node.name.name)) {
                return Error("Undefined variable: '${node.name.name}'")
            }

            val result = visitExpression(node.value)
            if (result is Error) return result

            val value = (result as? Success<*>)?.value
            symbolTable[node.name.name] = value

            Success(value)
        } catch (e: Exception) {
            Error("Error assigning variable '${node.name.name}': ${e.message}")
        }
    }

    override fun visitFunctionCall(node: FunctionCall): Results {
        return try {
            val evaluatedArgs = node.value.let {
                val result = visitExpression(it)
                if (result is Error) return result
                (result as? Success<*>)?.value
            }

            val result = when (node.identifier.name) {
                "print", "println" -> {
                    val output = evaluatedArgs.joinToString(" ") { it?.toString() ?: "null" }
                    println(output)
                    output
                }
                else -> return Error("Function '${node.identifier}' not defined")

            }

            Success(result)
        } catch (e: Exception) {
            Error("Error calling function '${node.identifier}': ${e.message}")
        }
    }

    override fun visitBinaryExpression(node: BinaryExpression): Results {
        return try {
            val leftResult = visitExpression(node.left)
            if (leftResult is Error) return leftResult

            val rightResult = visitExpression(node.right)
            if (rightResult is Error) return rightResult

            val leftValue = (leftResult as? Success<*>)?.value
            val rightValue = (rightResult as? Success<*>)?.value

            val result = when (node.operator) {
                Operator.ADD -> when {
                    leftValue is Number && rightValue is Number ->
                        leftValue.toDouble() + rightValue.toDouble()
                    else -> leftValue.toString() + rightValue.toString()
                }
                Operator.SUB -> (leftValue as Number).toDouble() - (rightValue as Number).toDouble()
                Operator.MUL -> (leftValue as Number).toDouble() * (rightValue as Number).toDouble()
                Operator.DIV -> {
                    val divisor = (rightValue as Number).toDouble()
                    if (divisor == 0.0) throw ArithmeticException("Division by zero")
                    (leftValue as Number).toDouble() / divisor
                }
                Operator.MOD -> (leftValue as Number).toDouble() % (rightValue as Number).toDouble()
            }

            Success(result)
        } catch (e: Exception) {
            Error("Error evaluating binary expression: ${e.message}")
        }
    }


     fun visitExpression(expr: Expression): Results {
        return when (expr) {
            is BinaryExpression -> visitBinaryExpression(expr)
            is IdentifierExpression -> visitIdentifierExpression(expr)
            is LiteralExpression<*> -> visitLiteralExpression(expr)
            else -> Error("Unknown expression type: ${expr::class.simpleName}")
        }
    }

    override fun visitIdentifierExpression(node: IdentifierExpression): Results {
        return try {
            val value = symbolTable[node.name]
            if (value == null && !symbolTable.containsKey(node.name)) {
                Error("Undefined variable: '${node.name}'")
            } else {
                Success(value)
            }
        } catch (e: Exception) {
            Error("Error accessing variable '${node.name}': ${e.message}")
        }
    }

    override fun visitLiteralExpression(node: LiteralExpression<*>): Results {
        return try {
            Success(node.value)
        } catch (e: Exception) {
            Error("Error evaluating literal: ${e.message}")
        }
    }
    override fun visitProgram(node: Program): Results {
        var lastResult: Results = NoResult()
        for (statement in node.statements) {
            val result = visitStatement(statement)
            if (result is Error) return result
            lastResult = result
        }
        return lastResult
    }

}
