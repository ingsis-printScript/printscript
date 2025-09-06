package org.example.parser.validators

import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.parser.exceptions.SyntaxException

class ExpressionValidator(val terminatorToken: Token) : TokenValidator {

    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        var pos = position
        var expectingElement = true
        val parenStack = ArrayDeque<Token>()

        while (statementBuffer.hasNext()) {
            val token = statementBuffer.lookahead(pos)

            if (isTerminatorToken(token)) break

            if (expectingElement) {
                when {
                    TokenType.isElement(token.type) -> {
                        expectingElement = false
                        validateElementToken(statementBuffer, pos)
                        pos++
                    }
                    isStartingParen(token) -> {
                        parenStack.addLast(token)
                        validatePunctuationToken(statementBuffer, pos, "(")
                        pos++
                    }
                    else -> return error(pos, "Expected element or '(', found '${token.value}'")
                }
            } else {
                when {
                    isOperator(token) -> {
                        expectingElement = true
                        validateOperatorToken(statementBuffer, pos)
                        pos++
                    }
                    isClosingParen(token) -> {
                        validatePunctuationToken(statementBuffer, pos, ")")
                        if (parenStack.isEmpty()) return error(pos, "Unmatched closing parenthesis ')'")
                        parenStack.removeLast()
                        pos++
                    }
                    else -> return error(pos, "Expected operator or ')', found '${token.value}'")
                }
            }
        }

        if (parenStack.isNotEmpty()) return error(pos, "Unmatched opening parenthesis '('")

        if (expectingElement && pos > position) return error(pos, "Expression cannot end with an operator")

        return ValidationResult.Success(pos - position)
    }

    private fun isSemicolon(token: Token) = isPunctuation(token) && token.value == ";"

    private fun error(pos: Int, message: String): ValidationResult.Error {
        return ValidationResult.Error(message, pos)
    }

    private fun isOperator(token: Token) = token.type == TokenType.OPERATOR

    private fun isClosingParen(token: Token) = isPunctuation(token) && token.value == ")"

    private fun isPunctuation(token: Token) = token.type == TokenType.PUNCTUATION

    private fun isStartingParen(token: Token) = isPunctuation(token) && token.value == "("

    private fun validateElementToken(tokens: TokenBuffer, pos: Int) {
        val token = tokens.lookahead(pos)
        when (token.type) {
            TokenType.STRING -> {
                StringValidator().validate(tokens, pos).let {
                    if (it is ValidationResult.Error) throw SyntaxException(it.message)
                }
            }
            TokenType.NUMBER -> {
                NumberValidator().validate(tokens, pos).let {
                    if (it is ValidationResult.Error) throw SyntaxException(it.message)
                }
            }
            TokenType.SYMBOL -> {
                SymbolValidator().validate(tokens, pos).let {
                    if (it is ValidationResult.Error) throw SyntaxException(it.message)
                }
            }

            else -> {throw SyntaxException("Expecting element, got '${token.value}'")}
        }
    }

    private fun validateOperatorToken(tokens: TokenBuffer, pos: Int) {
        OperatorValidator().validate(tokens, pos).let {
            if (it is ValidationResult.Error) throw SyntaxException(it.message)
        }
    }

    private fun validatePunctuationToken(tokens: TokenBuffer, pos: Int, expected: String) {
        PunctuationValidator(expected).validate(tokens, pos).let {
            if (it is ValidationResult.Error) throw SyntaxException(it.message)
        }
    }

    private fun isTerminatorToken(token: Token) = token.type == terminatorToken.type && token.value == terminatorToken.value

    override fun getExpectedDescription(): String {
        return "An expression with elements, operators, parentheses ending with ';'"
    }
}
