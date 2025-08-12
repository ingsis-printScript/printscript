package org.example.interpreter

import org.example.common.ast.ASTNode

//Para errores que saltan durante la ejecución
//No Result
class StaticInterpreter: Interpreter {
    override fun interpretate(ast: ASTNode): Result<Any> {
        TODO("Not yet implemented")
    }
}