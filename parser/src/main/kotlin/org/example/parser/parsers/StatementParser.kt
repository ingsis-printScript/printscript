package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.token.Token

interface StatementParser {
    fun buildAST(statement: List<Token>): ASTNode
    fun getPattern(): StatementPattern
}