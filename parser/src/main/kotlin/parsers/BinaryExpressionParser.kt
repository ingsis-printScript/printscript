package org.example.parser.parsers

import org.example.common.ast.ASTNode
import org.example.common.tokens.Token
import org.example.parser.validators.OperatorValidator
import org.example.parser.validators.ValidationResult

class BinaryExpressionParser : StatementParser {
    //TODO("
    //      Implement BinaryExpressionParser
    //      Implement ExpressionValidator
    //      Implement LiteralValidator?
    // ")
    private val pattern = StatementPattern(listOf(
        ExpressionValidator(),
        OperatorValidator(),
        ExpressionValidator()
    ))

    override fun canParse(statement: List<Token>): Boolean {
        TODO("Maybe mirar que haya BaseExpr, Op, BaseExpr")
    }

    override fun analyzeStatement(statement: List<Token>): ValidationResult {
        TODO("Not yet implemented")
    }

    override fun buildAST(statement: List<Token>): ASTNode {
        TODO("Not yet implemented")
    }

    override fun getPattern(): StatementPattern = pattern
}