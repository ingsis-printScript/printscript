package org.example.parser.parsers.builders.expression.rules

import org.example.common.enums.Operator

class OperatorPrecedence {
    private val precedenceMap = mapOf(
        Operator.MUL to 3,
        Operator.DIV to 3,
        Operator.MOD to 3,
        Operator.ADD to 2,
        Operator.SUB to 2
    )

    fun getPrecedence(operator: Operator): Int {
        return precedenceMap[operator] ?: 0
    }
}
