package org.example.interpreter

import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator
import org.example.common.results.Error
import org.example.common.results.Result
import org.example.common.results.Success

class Interpreter(
    private val iterator: PrintScriptIterator<Result>,
    private val validator: Validator,
    private val executor: Executor
) : PrintScriptIterator<Result> {

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun getNext(): Result {
        val result = iterator.getNext()

        if (result is Error) {
            executor.errorHandler.handleError(result.message)
            return result
        } else {
            val node = (result as? Success<ASTNode>)?.value
                ?: return result

            // Primero validamos usando Visitor
            node.accept(validator)

            // Luego ejecutamos usando Visitor
            return node.accept(executor).let { Success(node) }
        }
    }


    fun run(): List<Result> {
        val results = mutableListOf<Result>()
        while (hasNext()) {
            results.add(getNext())
        }
        return results
    }
}
