package org.example.linter.rules

import org.example.ast.ASTNode
import org.example.linter.LinterConfiguration
import org.example.linter.data.LinterViolation

interface Rule {
    fun check(node: ASTNode, configuration: LinterConfiguration): List<LinterViolation>
    fun isEnabled(configuration: LinterConfiguration): Boolean
}
