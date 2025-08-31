package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.token.Token

interface StatementParser {
    fun canParse(statement: List<Token>): Boolean

    // fun analyzeStatement(statement: List<Token>): ValidationResult
    fun buildAST(statement: List<Token>): ASTNode
    fun getPattern(): StatementPattern
}

// TODO(Impl para VAssignment, VarDec+Assignment, FunctionCall, Expression)
