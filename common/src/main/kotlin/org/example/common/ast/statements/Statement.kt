package org.example.common.ast.statements

import org.example.common.Range
import org.example.common.ast.ASTNode

interface Statement : ASTNode {
    val range: Range
}
