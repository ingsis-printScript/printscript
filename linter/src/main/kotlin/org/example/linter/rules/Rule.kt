package org.example.linter.rules

import org.example.ast.ASTNode
import org.example.ast.visitors.ASTVisitor
import org.example.linter.data.LinterViolation

interface Rule: ASTVisitor<List<LinterViolation>> {
    fun check(node: ASTNode): List<LinterViolation>
    fun isEnabled(): Boolean
}