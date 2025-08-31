package org.example.interpreter.handlers

import org.example.ast.statements.VariableAssigner
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class VariableAssignerHandler : ASTNodeHandler<VariableAssigner> {
    override fun handleExecution(node: VariableAssigner, executor: Executor) {
        val value = executor.evaluate(node.value)
        executor.assignVariable(node.symbol.name, value)
    }

    override fun handleValidators(node: VariableAssigner, validator: Validator) {
        TODO("Not yet implemented")
    }
}
