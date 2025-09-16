package org.example.interpreter

import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator
import org.example.common.results.Error
import org.example.common.results.Result
import org.example.common.results.Success

class Interpreter(
    private val iterator: PrintScriptIterator<Result>,
    private val validator: Validator,
    private val executor: Executor,
    private val supportedNodes: Set<Class<out ASTNode>>
) : PrintScriptIterator<Result> {

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun getNext(): Result {
        val result = iterator.getNext()

        val node: ASTNode = when (result) {
            is Success<*> ->
                result.value as? ASTNode
                    ?: return Error("The result is not an AST node")
            else -> return result
        }

        if (!supportedNodes.contains(node::class.java)) {
            return Error("The node: ${node::class.simpleName} isn't supported by this version")
        }

        node.accept(validator)
        node.accept(executor)

        return Success(node)
    }

    fun run(): List<Result> {
        val results = mutableListOf<Result>()
        while (hasNext()) {
            results.add(getNext())
        }
        return results
    }
}
