package org.example.interpreter

import org.example.common.ast.ASTNode
import org.example.common.enums.Type
import org.example.interpreter.handlers.ASTNodeHandler

public class Executor (
    private val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>>
){
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

    private fun visit(node: ASTNode) {
    }

}
