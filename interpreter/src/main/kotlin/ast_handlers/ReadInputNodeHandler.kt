package org.example.interpreter.ast_handlers

import org.example.ast.expressions.ReadInputNode
import org.example.common.enums.Type
import org.example.common.results.Error
import org.example.common.results.Success
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

class ReadInputNodeHandler : ASTNodeHandler<ReadInputNode> {

    override fun handleExecution(node: ReadInputNode, executor: Executor) {
        val input = executor.inputProvider.readInput(node.prompt)

        val value = when (node.expectedType) {
            Type.NUMBER -> input.toIntOrNull() ?: run {
                executor.reportError("Expected NUMBER but got '$input'")
                return
            }
            Type.BOOLEAN -> when (input.lowercase()) {
                "true" -> true
                "false" -> false
                else -> {
                    executor.reportError("Expected BOOLEAN but got '$input'")
                    return
                }
            }
            Type.STRING -> input
        }

        executor.pushLiteral(value)
    }

    override fun handleValidation(node: ReadInputNode, validator: Validator) {
        validator.pushLiteral(node.expectedType)
    }
}
