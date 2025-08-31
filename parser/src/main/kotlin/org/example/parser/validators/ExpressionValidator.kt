package org.example.parser.validators

import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.parser.ValidationResult
import org.example.parser.exceptions.SyntaxException

class ExpressionValidator : TokenValidator {

    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        var pos = position
        var expectingElement = true
        val parenStack = ArrayDeque<Token>()

        while (pos < statement.size) {
            val token = statement[pos] // peek

            if (isSemicolon(token)) break

            if (expectingElement) {
                when {
                    TokenType.isElement(token.type) -> {
                        expectingElement = false
                        validateElementToken(statement, pos)
                        pos++
                    }
                    isStartingParen(token) -> {
                        parenStack.addLast(token)
                        validatePunctuationToken(statement, pos, "(")
                        pos++
                    }
                    else -> return error(pos, "Expected element or '(', found '${token.value}'")
                }
            } else {
                when {
                    isOperator(token) -> {
                        expectingElement = true
                        validateOperatorToken(statement, pos)
                        pos++
                    }
                    isClosingParen(token) -> {
                        validatePunctuationToken(statement, pos, ")")
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
        // ver como manejar pos usando buffer, quizas mantener pos y ademas hacer advance

        return ValidationResult.Success(pos - position) // ver como
    }

    private fun isSemicolon(token: Token) = isPunctuation(token) && token.value == ";"

    private fun error(pos: Int, message: String): ValidationResult.Error {
        return ValidationResult.Error(message, pos)
    }

    private fun isOperator(token: Token) = token.type == TokenType.OPERATOR

    private fun isClosingParen(token: Token) = isPunctuation(token) && token.value == ")"

    private fun isPunctuation(token: Token) = token.type == TokenType.PUNCTUATION

    private fun isStartingParen(token: Token) = isPunctuation(token) && token.value == "("

    private fun validateElementToken(tokenList: List<Token>, pos: Int) {
        val token = tokenList[pos]
        when (token.type) {
            TokenType.STRING -> {
                StringValidator().validate(tokenList, pos).let {
                    if (it is ValidationResult.Error) throw SyntaxException(it.message)
                }
            }
            TokenType.NUMBER -> {
                NumberValidator().validate(tokenList, pos).let {
                    if (it is ValidationResult.Error) throw SyntaxException(it.message)
                }
            }
            TokenType.SYMBOL -> {
                SymbolValidator().validate(tokenList, pos).let {
                    if (it is ValidationResult.Error) throw SyntaxException(it.message)
                }
            }

            else -> {throw SyntaxException("Expecting element, got '${token.value}'")}
        }
    }

    private fun validateOperatorToken(tokenList: List<Token>, pos: Int) {
        OperatorValidator().validate(tokenList, pos).let {
            if (it is ValidationResult.Error) throw SyntaxException(it.message)
        }
    }

    private fun validatePunctuationToken(tokenList: List<Token>, pos: Int, expected: String) {
        PunctuationValidator(expected).validate(tokenList, pos).let {
            if (it is ValidationResult.Error) throw SyntaxException(it.message)
        }
    }

    override fun getExpectedDescription(): String {
        return "An expression with elements, operators, parentheses ending with ';'"
    }
}
