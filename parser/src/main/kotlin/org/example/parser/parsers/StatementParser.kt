package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.token.Token

interface StatementParser {
    fun buildAST(statements: List<Token>): ASTNode
    fun getPatterns(): List<StatementPattern>
}