package org.example.parser.parsers

import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.parser.validators.TokenValidator
import org.example.token.Token

data class StatementPattern(val validators: List<TokenValidator>) {

    fun analyzeStatement(buffer: TokenBuffer, start: Int): ValidationResult {
        var position = start
        val consumed = mutableListOf<Token>()

        for (validator in this.validators) {
            when (val result = validator.validate(buffer, position)) {
                is ValidationResult.Error -> return result
                is ValidationResult.Success -> {
                    position += result.consumed.size
                    consumed.addAll(result.consumed)
                }
            }
        }
        return ValidationResult.Success(consumed)
    }
}
