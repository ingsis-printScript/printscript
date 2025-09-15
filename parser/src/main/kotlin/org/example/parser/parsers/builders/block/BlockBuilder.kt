package org.example.parser.parsers.builders.block

import org.example.ast.ASTNode
import org.example.common.results.Success
import org.example.parser.Parser
import org.example.parser.TokenBuffer
import org.example.parser.parsers.StatementParser
import org.example.token.Token

class BlockBuilder(
    private val statementParsers: List<StatementParser>
) {
    fun build(tokens: List<Token>): List<ASTNode> {
        val buffer = TokenBuffer(ListIterator(tokens))
        val parser = Parser(statementParsers, buffer)

        val statements = mutableListOf<ASTNode>()

        while (!buffer.isAtEnd()) {
            statements.add((parser.getNext() as Success<ASTNode>).value)
        }

        return statements
    }
}
