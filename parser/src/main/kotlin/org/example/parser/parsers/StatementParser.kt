package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.parser.TokenBuffer
import org.example.token.Token

interface StatementParser {
    fun buildAST(statementBuffer: List<Token>): ASTNode
    fun getPattern(): StatementPattern
}