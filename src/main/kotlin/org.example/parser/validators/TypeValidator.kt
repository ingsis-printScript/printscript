package org.example.parser.validators

import org.example.common.tokens.Token
import org.example.common.tokens.TypeToken
import org.example.common.tokens.enums.Types

class TypeValidator() : TokenValidator {
    // TODO(evaluar si cambiar enum por list de validTypes -> private val validTypes: List<String>)
    private val validTypes = Types.values().map { it.name.lowercase() }

    //private val validTypesMessage: String = validTypes.joinToString(", ", prefix = "(", postfix = ")")


    override fun validate(token: Token, position: Int): ValidationResult {
        return when (token) {
            is TypeToken -> {
                if (token.kind.name in validTypes) {
                    ValidationResult.Success
                } else {
                    ValidationResult.Error("Expected type $validTypes, found '${token.kind.name}'", position)
                }
            }
            else -> ValidationResult.Error("Expected type annotation, found ${token::class.simpleName}", position)
        }
    }

    override fun getExpectedDescription(): String = "Expected type annotation $validTypes"
}