package org.example.formatter.formatters

import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.Expression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.formatter.Rule
import java.io.Writer

class ExpressionFormatterHelper {

    fun formatExpression(node: Expression, writer: Writer, rules: Map<String, Rule>, nestingLevel: Int) {
        when (node) {
            is BinaryExpression -> {
                BinaryExpressionFormat().formatNode(node, writer, rules, nestingLevel)
            }
            is BooleanExpression -> {
                BooleanExpressionFormat().formatNode(node, writer, rules, nestingLevel)
            }
            is NumberExpression -> {
                NumberExpressionFormat().formatNode(node, writer, rules, nestingLevel)
            }
            is StringExpression -> {
                StringExpressionFormat().formatNode(node, writer, rules, nestingLevel)
            }
            is SymbolExpression -> {
                SymbolExpressionFormat().formatNode(node, writer, rules, nestingLevel)
            }
            else -> throw IllegalArgumentException("Unsupported expression type: ${node::class.simpleName}")
        }
    }
}
