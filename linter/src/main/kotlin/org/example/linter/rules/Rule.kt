package org.example.linter.rules

import org.example.ast.ASTNode
import org.example.common.ErrorHandler
import org.example.common.results.Result
import org.example.linter.LinterConfiguration

interface Rule {
    fun check(node: ASTNode, configuration: LinterConfiguration, errorHandler: ErrorHandler): Result
    fun isEnabled(configuration: LinterConfiguration): Boolean
}
