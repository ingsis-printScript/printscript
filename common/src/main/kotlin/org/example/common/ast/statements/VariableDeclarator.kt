package org.example.common.ast.statements

import org.example.common.Range

class VariableDeclarator(
    val type: String,
    override val range: Range,
    val identifier: String,
    ): Statement