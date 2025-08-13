package org.example.common.ast

import org.example.common.ast.statements.Statement

class Program(
    val statements: List<Statement>
): ASTNode {
}