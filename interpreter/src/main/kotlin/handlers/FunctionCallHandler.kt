package org.example.interpreter.handlers

import org.example.ast.statements.functions.FunctionCall
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.result.Results
import org.example.interpreter.result.Success
import org.example.interpreter.result.Error
import org.example.interpreter.result.NoResult

class FunctionCallHandler : ASTNodeHandler<FunctionCall> {
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
