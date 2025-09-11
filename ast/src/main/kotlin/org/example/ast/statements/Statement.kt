package org.example.ast.statements

import org.example.ast.ASTNode
import org.example.common.Range

interface Statement : ASTNode {
    val range: Range
}
