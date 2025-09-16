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
import org.example.common.enums.Type

class Validator(
    val errorHandler: ErrorHandler
) : ASTVisitor<ASTNode> {

    private val stack = mutableListOf<Type?>()
    private val environment = mutableMapOf<String, Type>()

    fun evaluate(node: ASTNode): Type? {
        node.accept(this)
        return popLiteral()
    }

    fun pushLiteral(value: Type?) = stack.add(value)
    fun popLiteral(): Type? = if (stack.isEmpty()) null else stack.removeAt(stack.size - 1)

    fun lookupSymbol(name: String): Type? {
        return environment[name]
    }

    fun reportError(message: String) {
        errorHandler.handleError(message)
    }

    override fun visitVariableDeclarator(statement: VariableDeclarator): ASTNode {
        val type = when (val opt = statement.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression)
            is OptionalExpression.NoExpression -> null
        }

        environment[statement.symbol.value] = type ?: statement.type
        return statement
    }

    override fun visitVariableImmutableDeclarator(statement: VariableImmutableDeclarator): ASTNode {
        val type = when (val opt = statement.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression)
            is OptionalExpression.NoExpression -> null
        }

        if (environment.containsKey(statement.symbol.value)) {
            reportError("Immutable variable ${statement.symbol.value} already declared")
        } else {
            environment[statement.symbol.value] = type ?: statement.type
        }

        return statement
    }

    override fun visitVariableAssigner(statement: VariableAssigner): ASTNode {
        val type = when (val opt = statement.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression)
            is OptionalExpression.NoExpression -> null
        }
        if (type != null) {
            if (!environment.containsKey(statement.symbol.value)) {
                reportError("Variable ${statement.symbol.value} not declared")
            } else {
                pushLiteral(type)
            }
        }
        return statement
    }

    override fun visitSymbol(expr: SymbolExpression): ASTNode {
        val type = lookupSymbol(expr.value)
        pushLiteral(type)
        return expr
    }

    override fun visitBoolean(expr: BooleanExpression): ASTNode {
        pushLiteral(Type.BOOLEAN)
        return expr
    }

    override fun visitNumber(expr: NumberExpression): ASTNode {
        pushLiteral(Type.NUMBER)
        return expr
    }

    override fun visitString(expr: StringExpression): ASTNode {
        pushLiteral(Type.STRING)
        return expr
    }

    override fun visitReadInput(expr: ReadInputExpression): ASTNode {
        pushLiteral(Type.STRING)
        return expr
    }

    override fun visitReadEnv(expr: ReadEnvExpression): ASTNode {
        val type = when (val opt = expr.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression)
            is OptionalExpression.NoExpression -> null
        }

        pushLiteral(type ?: Type.STRING)

        return expr
    }

    override fun visitPrintFunction(statement: PrintFunction): ASTNode {
        when (val opt = statement.value) {
            is OptionalExpression.HasExpression -> evaluate(opt.expression)
            is OptionalExpression.NoExpression -> pushLiteral(null)
        }
        return statement
    }

    override fun visitCondition(statement: Condition): ASTNode {
        evaluate(statement.condition)
        statement.ifBlock.forEach { evaluate(it) }
        statement.elseBlock?.forEach { evaluate(it) }
        return statement
    }

    override fun visitBinary(expr: BinaryExpression): ASTNode {
        val leftType = evaluate(expr.left)
        val rightType = evaluate(expr.right)

        val resultType = when (expr.operator) {
            Operator.ADD -> if (leftType == Type.STRING || rightType == Type.STRING) Type.STRING else Type.NUMBER
            Operator.SUB, Operator.MUL, Operator.DIV, Operator.MOD -> {
                if (leftType != Type.NUMBER || rightType != Type.NUMBER) {
                    reportError("Binary operation ${expr.operator} requires numeric operands")
                }
                Type.NUMBER
            }
        }

        pushLiteral(resultType)
        return expr
    }
}
