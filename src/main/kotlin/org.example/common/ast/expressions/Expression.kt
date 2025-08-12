package org.example.common.ast.expressions

import org.example.common.Range
import org.example.common.ast.ASTNode

interface Expression: ASTNode {
    val range: Range
}