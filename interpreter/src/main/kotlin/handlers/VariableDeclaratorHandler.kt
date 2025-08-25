package org.example.interpreter.handlers

import org.example.common.ast.statements.VariableDeclarator
import org.example.interpreter.Executor


class VariableDeclaratorHandler : ASTNodeHandler<VariableDeclarator> {
    override fun handleExecution(node: VariableDeclarator, executor: Executor) {
        val value = node.value?.let{executor.evaluate(it)}
        executor.declareVariable(node.identifier.name, node.type, value)
    }
}