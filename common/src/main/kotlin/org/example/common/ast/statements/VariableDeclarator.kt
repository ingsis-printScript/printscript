package org.example.common.ast.statements

import org.example.common.Range
import org.example.common.ast.expressions.Expression
import org.example.common.ast.expressions.IdentifierExpression
import org.example.parser.enums.Type

class VariableDeclarator(
    val name: IdentifierExpression,
    val type: Type,
    override val range: Range,
    val expression: Expression? = null,
    ): Statement