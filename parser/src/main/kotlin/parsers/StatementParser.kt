package org.example.parser.parsers

import org.example.common.ast.ASTNode
import org.example.common.tokens.Token
import org.example.parser.ValidationResult

interface StatementParser {
    fun canParse(statement: List<Token>): Boolean
    //fun analyzeStatement(statement: List<Token>): ValidationResult
    fun buildAST(statement: List<Token>): ASTNode
    fun getPattern(): StatementPattern
}

//TODO(Impl para VAssignment, VarDec+Assignment, FunctionCall, Expression)
