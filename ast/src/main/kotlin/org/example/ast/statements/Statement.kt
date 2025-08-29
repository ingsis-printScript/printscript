package org.example.ast.statements

import org.example.common.Range
import org.example.ast.ASTNode

interface Statement : ASTNode {
    val range: Range
}
