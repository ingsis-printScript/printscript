package org.example.common.ast.statements

import org.example.common.Range
import org.example.common.ast.expressions.Expression
import org.example.common.ast.expressions.IdentifierExpression

class VariableDeclarator(
    val identifier: IdentifierExpression,
    override val range: Range,
    val expression: Expression? = null,
    ): Statement