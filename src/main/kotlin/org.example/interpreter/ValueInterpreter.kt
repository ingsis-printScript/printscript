package org.example.interpreter

import org.example.common.ast.ASTNode

//para expressiones que dan un resultado
//Success or RunTime Error
class ValueInterpreter: Interpreter {
    override fun interpretate(ast: ASTNode): Result<Any> {
        TODO("Not yet implemented")
    }
}