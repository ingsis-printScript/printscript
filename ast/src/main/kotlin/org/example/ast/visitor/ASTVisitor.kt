package org.example.ast.visitor

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

interface ASTVisitor<T> {
    fun visitBinary(expr: BinaryExpression): T
    fun visitBoolean(expr: BooleanExpression): T
    fun visitNumber(expr: NumberExpression): T
    fun visitString(expr: StringExpression): T
    fun visitReadInput(expr: ReadInputExpression): T
    fun visitReadEnv(expr: ReadEnvExpression): T
    fun visitSymbol(expr: SymbolExpression): T

    fun visitPrintFunction(statement: PrintFunction): T
    fun visitCondition(statement: Condition): T
    fun visitVariableAssigner(statement: VariableAssigner): T
    fun visitVariableDeclarator(statement: VariableDeclarator): T
    fun visitVariableImmutableDeclarator(statement: VariableImmutableDeclarator): T
}
