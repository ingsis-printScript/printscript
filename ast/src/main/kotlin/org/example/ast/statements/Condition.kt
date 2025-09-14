package org.example.ast.statements

import org.example.ast.ASTNode
import org.example.ast.expressions.BooleanExpression
import org.example.common.Range

class Condition(
    val condition: BooleanExpression,
    val ifBlock: List<ASTNode>,
    val elseBlock: List<ASTNode>?,
    override val range: Range
) : Statement {
}
