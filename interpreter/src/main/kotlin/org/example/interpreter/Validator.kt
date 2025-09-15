package org.example.interpreter

import org.example.ast.ASTNode
import org.example.common.enums.Type
import org.example.common.results.NoResult
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.output.ErrorHandler

class Validator(
    private val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>>,
    val errorHandler: ErrorHandler
) {

    private val stack = mutableListOf<Type?>()
    private val environment = mutableMapOf<String, Type>() // ahora guarda Type
    private var lastResult: Result = NoResult()

    fun processNode(node: ASTNode): Result {
        val handler = handlers[node::class.java] as ASTNodeHandler<ASTNode>
        handler.handleValidation(node, this)
        return Success(Unit)
    }

    fun evaluate(node: ASTNode): Type? {
        processNode(node)
        return popLiteral()
    }

    fun declareVariable(name: String, type: Type) {
        if (environment.containsKey(name)) {
            throw RuntimeException("Variable $name ya declarada")
        }
        environment[name] = type
    }

    fun lookupVariable(name: String): Type {
        return environment[name] ?: throw RuntimeException("Variable $name no declarada")
    }

    fun pushLiteral(value: Type?) = stack.add(value)
    fun popLiteral(): Type? = if (stack.isEmpty()) null else stack.removeAt(stack.size - 1)

    fun returnResult(result: Result) {
        lastResult = result
    }

    fun lookupSymbol(name: String): Type? {
        return environment[name]
    }

    fun reportError(message: String) {
        errorHandler.handleError(message)
    }
}
