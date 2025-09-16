package org.example.formatter.formatters

import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.Expression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.ReadEnvExpression
import org.example.ast.expressions.ReadInputExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.formatter.PrivateIterator
import org.example.formatter.Rule
import java.io.Writer

class ExpressionFormatterHelper {

    fun formatExpression(node: Expression, writer: Writer, rules: Map<String, Rule>, nestingLevel: Int, context: PrivateIterator) {
        when (node) {
            is BinaryExpression -> {
                BinaryExpressionFormat().formatNode(node, writer, rules, nestingLevel, context)
            }
            is BooleanExpression -> {
                BooleanExpressionFormat().formatNode(node, writer, rules, nestingLevel, context)
            }
            is NumberExpression -> {
                NumberExpressionFormat().formatNode(node, writer, rules, nestingLevel, context)
            }
            is StringExpression -> {
                StringExpressionFormat().formatNode(node, writer, rules, nestingLevel, context)
            }
            is SymbolExpression -> {
                SymbolExpressionFormat().formatNode(node, writer, rules, nestingLevel, context)
            }
            is ReadEnvExpression -> {
                ReadEnvExpressionFormat().formatNode(node, writer, rules, nestingLevel, context)
            }
            is ReadInputExpression -> {
                ReadInputExpressionFormat().formatNode(node, writer, rules, nestingLevel, context)
            }
            else -> throw IllegalArgumentException("Unsupported expression type: ${node::class.simpleName}")
        }
    }
}
