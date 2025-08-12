package org.example.interpreter

import org.example.common.ast.ASTNode

interface Interpreter {
    fun interpretate(ast: ASTNode): Result<Any>

}