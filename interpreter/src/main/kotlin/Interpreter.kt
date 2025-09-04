package org.example.interpreter

import org.example.common.PrintScriptIterator
import org.example.ast.ASTNode
import org.example.common.results.Result

class Interpreter(
    private val iterator: PrintScriptIterator<ASTNode>,
    private val validator: Validator,
    private val executor: Executor
) : PrintScriptIterator<Result> {

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun getNext(): Result {
        val node = iterator.getNext()

        // Primero validamos el nodo
        validator.visit(node)

        // Luego ejecutamos el nodo
        return executor.visit(node)
    }
}
