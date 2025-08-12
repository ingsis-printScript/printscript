package org.example.interpreter

import org.example.common.ast.ASTNode

//Para leer statements tipo let x = 5
//No Result or RunTimeError
class LiteralInterpreter: Interpreter {
    override fun interpretate(ast: ASTNode): Result<Any> {
        TODO("Not yet implemented")
    }
}