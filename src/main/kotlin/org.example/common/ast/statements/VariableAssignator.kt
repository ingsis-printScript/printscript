package org.example.common.ast.statements

import org.example.common.Range
import org.example.common.ast.expressions.Expression

class VariableAssignator(
    val identifier: String,
    val value: Expression,
    override val range: Range
    ): Statement {
}