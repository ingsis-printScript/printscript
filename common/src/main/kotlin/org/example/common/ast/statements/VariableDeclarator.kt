package org.example.common.ast.statements

import org.example.common.Position
import org.example.common.ast.expressions.Expression
import org.example.common.ast.expressions.IdentifierExpression
import org.example.parser.enums.Type

class VariableDeclarator(
    val name: IdentifierExpression,
    val type: Type,
    override val position: Position,
    val expression: Expression? = null,
    ): Statement