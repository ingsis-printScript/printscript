package org.example.interpreter

import org.example.ast.ASTNode
import org.example.common.enums.Type
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.result.NoResult
import org.example.interpreter.result.Results
import org.example.interpreter.result.Success
import org.example.interpreter.visitors.ASTVisitor

class Validator(
    private val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>>
) : ASTVisitor<Results> {

    private val stack = mutableListOf<Type?>()
    private val environment = mutableMapOf<String, Type>()  // ahora guarda Type
    private var lastResult: Results = NoResult()

    override fun visit(node: ASTNode): Results {
        val handler = handlers[node::class.java] as ASTNodeHandler<ASTNode>
        handler.handleValidators(node, this)
        return Success(Unit)
    }

    fun evaluate(node: ASTNode): Type? {
        node.accept(this)
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

    fun pushLiteral(value: Type?) {
        stack.add(value)
    }

    fun popLiteral(): Type? {
        if (stack.isEmpty()) return null
        return stack.removeAt(stack.size - 1)
    }

    fun returnResult(result: Results) {
        lastResult = result
    }

    fun lookupSymbol(name: String): Type? {
        return environment[name] // ✅ ahora sí existe
    }
}

