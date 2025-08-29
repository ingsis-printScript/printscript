package org.example.interpreter.handlers

import org.example.common.ast.statements.VariableDeclarator
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class VariableDeclaratorHandler : ASTNodeHandler<VariableDeclarator> {
    override fun handleExecution(node: VariableDeclarator, executor: Executor) {
        val value = node.value?.let { executor.evaluate(it) }
        executor.declareVariable(node.identifier.name, node.type, value)
    }

    override fun handleValidators(node: VariableDeclarator, validator: Validator) {
        TODO("Not yet implemented")
    }
}
