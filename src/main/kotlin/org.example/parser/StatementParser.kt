package org.example.parser

import org.example.common.ast.ASTnode
import org.example.common.tokens.Token

interface StatementParser {
    fun canParse(statement: List<Token>): Boolean
    fun analyzeStatement(statement: List<Token>): Boolean
    fun buildAST(statement: List<Token>): ASTnode
    fun getPattern(): StatementPattern
}

//implementacion