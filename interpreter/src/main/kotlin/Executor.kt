package org.example.interpreter

import org.example.ast.ASTNode
import org.example.common.enums.Type
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.result.Results
import org.example.interpreter.visitors.ASTVisitor

class Executor : ASTVisitor<>{
    private val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>>
    private val environment = mutableMapOf<String, Any?>()
    private val stack = mutableListOf<Any?>()

    fun execute(node: ASTNode) {
    }

    fun evaluate(node: ASTNode): Any? {
        return null
    }

    fun assignVariable(name: String, value: Any?) {
    }

    fun lookupVariable(name: String): Any? {
        return null
    }

    fun declareVariable(name: String, type: Type, value: Any?) {
        if (environment.containsKey(name)) {
            throw RuntimeException("Variable $name ya declarada")
        }
        environment[name] = value
    }

    fun pushLiteral(value: Any?) {
    }

    fun popLiteral(): Any? {
        return null
    }

    fun output(value: String) {
    }

    override fun visit(node: ASTNode): Results {
        TODO("Not yet implemented")
    }

}
