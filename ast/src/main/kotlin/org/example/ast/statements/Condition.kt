package org.example.ast.statements

import org.example.ast.ASTNode
import org.example.ast.expressions.OptionalExpression
import org.example.ast.visitor.ASTVisitor
import org.example.common.Range

class Condition(
    val condition: OptionalExpression,
    val ifBlock: List<ASTNode>,
    val elseBlock: List<ASTNode>?,
    override val range: Range
) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitCondition(this)
    }
}
