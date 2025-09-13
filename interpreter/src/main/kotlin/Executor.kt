package org.example.interpreter

import org.example.ast.ASTNode
import org.example.ast.visitors.ASTVisitor
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.ErrorHandler
import org.example.interpreter.output.OutputPrinter

class Executor(
    private val handlers: Map<Class<out ASTNode>, ASTNodeHandler<*>>,
    val inputProvider: InputProvider,
    val outputPrinter: OutputPrinter,
    val errorHandler: ErrorHandler
) : ASTVisitor<Result> {

    private val environment = mutableMapOf<String, Any?>()
    private val stack = mutableListOf<Any?>()

    fun pushLiteral(value: Any?) = stack.add(value)
    fun popLiteral(): Any? = if (stack.isEmpty()) null else stack.removeAt(stack.size - 1)

    fun declareVariable(name: String, value: Any?) {
        if (environment.containsKey(name)) throw RuntimeException("Variable $name already declared")
        environment[name] = value
    }

    fun assignVariable(name: String, value: Any?) {
        if (!environment.containsKey(name)) throw RuntimeException("Variable $name not declared")
        environment[name] = value
    }

    fun lookupVariable(name: String): Any? = environment[name]

    fun printValue(value: Any?) {
        outputPrinter.print(value.toString())  // manda solo a Printer
    }

    fun reportError(message: String) {
        errorHandler.handleError(message)
    }

    override fun visit(node: ASTNode): Result {
        val handler = handlers[node::class.java] as ASTNodeHandler<ASTNode>
        handler.handleExecution(node, this)
        return Success(Unit)
    }

    fun evaluate(node: ASTNode): Any? {
        node.accept(this)  // esto llama a visit(node)
        return popLiteral()
    }



    fun isVariableDeclared(name: String): Boolean {
        return environment.containsKey(name)
    }



}
