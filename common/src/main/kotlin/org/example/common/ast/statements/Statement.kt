package org.example.common.ast.statements

import org.example.common.Position
import org.example.common.ast.ASTNode

interface Statement: ASTNode {
    val position: Position
}