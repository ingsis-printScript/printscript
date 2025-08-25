package org.example.interpreter.handlers

import org.example.common.ast.statements.VariableAssigner
import org.example.interpreter.Executor


class VariableAssignerHandler : ASTNodeHandler<VariableAssigner> {
    override fun handleExecution(node: VariableAssigner, executor: Executor) {
        val value = executor.evaluate(node.value)
        executor.assignVariable(node.name.name, value)
    }
}