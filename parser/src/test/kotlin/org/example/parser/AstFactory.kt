package org.example.parser

import org.example.ast.expressions.Expression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.functions.PrintFunction
import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.Operator

class AstFactory {

    private val basicRange = Range(Position(1, 1), Position(1, 1))
    private val basicPosition = Position(1, 1)

    fun createNumber(value: String, position: Position = basicPosition): NumberExpression {
        return NumberExpression(value, position)
    }

    fun createString(value: String, position: Position = basicPosition): StringExpression {
        return StringExpression(value, position)
    }

    fun createSymbol(name: String, position: Position = basicPosition) = SymbolExpression(name, position)

    fun createBinaryExpression(left: Expression, operator: Operator, right: Expression, range: Range = basicRange) =
        org.example.ast.expressions.BinaryExpression(left, operator, right, range)

    fun createVariableAssigment(symbol: SymbolExpression, value: OptionalExpression, range: Range = basicRange) =
        VariableAssigner(symbol, value, range)

    fun createVariableDeclarator(
        symbol: SymbolExpression,
        type: org.example.common.enums.Type,
        value: OptionalExpression = OptionalExpression.NoExpression,
        range: Range = basicRange
    ) = VariableDeclarator(symbol, type, range, value)

    fun createPrintFunction(value: OptionalExpression, range: Range = basicRange) =
        PrintFunction(value, range)
}
