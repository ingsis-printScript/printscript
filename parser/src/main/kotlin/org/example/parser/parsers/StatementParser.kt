package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.parser.TokenBuffer

interface StatementParser {
    fun buildAST(statementBuffer: TokenBuffer): ASTNode
    fun getPattern(): StatementPattern
}