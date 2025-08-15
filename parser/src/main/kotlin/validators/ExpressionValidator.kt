package org.example.parser.validators

import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.ValidationResult

class ExpressionValidator : TokenValidator {

    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        var pos = position
        var expectingElement = true
        val parenStack = ArrayDeque<Token>()

        while (pos < statement.size) {
            val token = statement[pos]

            if (token.kind == TokenType.PUNCTUATION && token.name == ";") {
                break
            }

            if (expectingElement) {
                when {
                    TokenType.isElement(token.kind) -> {
                        expectingElement = false
                        pos++
                    }
                    token.kind == TokenType.PUNCTUATION && token.name == "(" -> {
                        parenStack.addLast(token)
                        pos++
                    }
                    else -> return ValidationResult.Error(
                        "Expected element or '(', found '${token.name}'", pos
                    )
                }
            } else {
                when {
                    token.kind == TokenType.OPERATOR -> {
                        expectingElement = true
                        pos++
                    }
                    token.kind == TokenType.PUNCTUATION && token.name == ")" -> {
                        if (parenStack.isEmpty()) {
                            return ValidationResult.Error("Unmatched closing parenthesis ')'", pos)
                        }
                        parenStack.removeLast()
                        pos++
                    }
                    else -> return ValidationResult.Error(
                        "Expected operator or ')', found '${token.name}'", pos
                    )
                }
            }
        }

        if (parenStack.isNotEmpty()) {
            return ValidationResult.Error("Unmatched opening parenthesis '('", pos)
        }

        if (expectingElement && pos > position) {
            return ValidationResult.Error("Expression cannot end with an operator", pos)
        }

        return ValidationResult.Success(pos - position)
    }

    override fun getExpectedDescription(): String {
        return "An expression with elements, operators, parentheses ending with ';'"
    }
}
