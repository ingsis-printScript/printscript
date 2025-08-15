package org.example.parser.parsers

import org.example.common.ast.ASTNode
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.ValidationResult
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.IdentifierValidator
import org.example.parser.validators.PunctuationValidator

class FunctionCallParser : StatementParser {
    private val pattern = StatementPattern(
        listOf(
            IdentifierValidator(),
            PunctuationValidator("("),
            ExpressionValidator(),
            PunctuationValidator(")"),
            PunctuationValidator(";")
        )
    )

    override fun canParse(statement: List<Token>): Boolean {
        return statement.isNotEmpty() &&
                statement[0].kind == TokenType.SYMBOL && // TODO(CHECK SYMBOL)
                statement[1].kind == TokenType.PUNCTUATION &&
                statement[1].name == "("
    }

    //override fun analyzeStatement(statement: List<Token>): ValidationResult {}

    override fun buildAST(statement: List<Token>): ASTNode {
        TODO("Not yet implemented")
    }

    override fun getPattern(): StatementPattern = pattern
}