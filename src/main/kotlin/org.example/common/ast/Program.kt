package org.example.common.ast

import org.example.common.Range
import org.example.common.ast.statements.Statement

class Program(
    override val range: Range,
    val statements: List<Statement>
): ASTnode {
}