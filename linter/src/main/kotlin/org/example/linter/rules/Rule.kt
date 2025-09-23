package org.example.linter.rules

import org.example.ast.ASTNode
import org.example.common.ErrorHandler
import org.example.common.configuration.RulesConfiguration
import org.example.common.results.Result

interface Rule {
    fun check(node: ASTNode, configuration: RulesConfiguration, errorHandler: ErrorHandler): Result
    fun isEnabled(configuration: RulesConfiguration): Boolean
}
