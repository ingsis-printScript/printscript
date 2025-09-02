package org.example.interpreter

import org.example.common.PrintScriptIterator
import org.example.ast.ASTNode
import org.example.interpreter.result.Results

class Interpreter(
    private val iterator: PrintScriptIterator<ASTNode>,
    private val validator: Validator,
    private val executor: Executor
) : PrintScriptIterator<Results> {

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun getNext(): Results {
        val node = iterator.getNext()

        // Primero validamos el nodo
        validator.visit(node)

        // Luego ejecutamos el nodo
        return executor.visit(node)
    }
}
