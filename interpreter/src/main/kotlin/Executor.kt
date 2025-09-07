package org.example.interpreter

import org.example.ast.ASTNode
import org.example.common.enums.Type
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.common.results.Result
import org.example.ast.visitors.ASTVisitor
import org.example.common.results.NoResult
import org.example.common.results.Success

class Executor (
    private val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>>
) : ASTVisitor<Result> {

    private val environment = mutableMapOf<String, Any?>()
    private val stack = mutableListOf<Any?>()
    private var lastResult: Result = NoResult()



    fun evaluate(node: ASTNode): Any? {
        node.accept(this)
        return popLiteral()
    }

    fun printValue(value: Any?) {
        println(value) // imprime en consola
    }


    fun declareVariable(name: String, type: Type, value: Any?) {
        if (environment.containsKey(name)) {
            throw RuntimeException("Variable $name has already been declared")
        }
        environment[name] = value
    }

    fun pushLiteral(value: Any?) {
        stack.add(value)
    }

    fun popLiteral(): Any? {
        if (stack.isEmpty()) {
            return null
        }
        return stack.removeAt(stack.size - 1)
    }


    override fun visit(node: ASTNode): Result {
        val handler = handlers[node::class.java] as ASTNodeHandler<ASTNode>
        handler.handleExecution(node, this)
        return Success(Unit)
    }

    fun returnResult(result: Result) {
        lastResult = result
    }

    fun lookupVariable(name: String): Any? {
        return environment[name]
    }

    fun assignVariable(name: String, value: Any?) {
        if (!environment.containsKey(name)) {
            throw RuntimeException("Variable $name not declared")
        }
        environment[name] = value
    }

    fun isVariableDeclared(name: String): Boolean {
        return environment.containsKey(name)
    }

}
