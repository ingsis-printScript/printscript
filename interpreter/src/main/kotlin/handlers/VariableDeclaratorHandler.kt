package org.example.interpreter.handlers

import org.example.ast.statements.VariableDeclarator
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class VariableDeclaratorHandler : ASTNodeHandler<VariableDeclarator> {

    override fun handleExecution(node: VariableDeclarator, executor: Executor) {
        val value: Any? = node.value?.let { executor.evaluate(it?) }

        executor.declareVariable(node.symbol.value, node.type, value)
    }

    override fun handleValidators(node: VariableDeclarator, validator: Validator) {
        validator.declareVariable(node.symbol.value, node.type)

        node.value?.let {
            val valueType = validator.evaluate(it) // debe devolver Type?
            if (valueType == null) {
                throw RuntimeException(
                    "Cannot determine type for variable '${node.symbol.value}'"
                )
            }

            if (valueType != node.type) {
                throw RuntimeException(
                    "Type mismatch in variable '${node.symbol.value}': expected ${node.type.name}, got ${valueType.name}"
                )
            }
        }
    }
}