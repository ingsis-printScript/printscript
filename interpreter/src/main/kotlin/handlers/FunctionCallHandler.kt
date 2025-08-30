package org.example.interpreter.handlers

import org.example.ast.statements.functions.FunctionCall
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class FunctionCallHandler : ASTNodeHandler<FunctionCall> {
    override fun handleExecution(node: FunctionCall, executor: Executor) {
        val arg = executor.evaluate(node.value)
        if (node.identifier.name == "print") {
            executor.output(arg.toString())
        } else {
            throw RuntimeException("Function ${node.identifier.name} not supported")
        }
    }

    override fun handleValidators(node: FunctionCall, validator: Validator) {
        TODO("Not yet implemented")
    }
}
