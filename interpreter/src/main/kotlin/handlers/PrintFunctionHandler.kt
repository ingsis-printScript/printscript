package org.example.interpreter.handlers

import org.example.ast.statements.functions.FunctionCall
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class PrintFunctionHandler : ASTNodeHandler<FunctionCall> {
    override fun handleExecution(
        node: FunctionCall,
        executor: Executor
    ) {
        TODO("Not yet implemented")
    }

    override fun handleValidators(
        node: FunctionCall,
        validator: Validator
    ) {
        TODO("Not yet implemented")
    }


}
