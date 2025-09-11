package org.example.parser.validators

import org.example.common.enums.TokenType
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token

class ExpressionValidator(
    private val isElement: (Token) -> Boolean = { t ->
        t.type == TokenType.NUMBER || t.type == TokenType.STRING || t.type == TokenType.SYMBOL
    },
    private val isOperator: (Token) -> Boolean = { t -> t.type == TokenType.OPERATOR },
    private val isGroupStart: (Token) -> Boolean = { t -> t.type == TokenType.PUNCTUATION && t.value == "(" },
    private val isGroupEnd: (Token) -> Boolean = { t -> t.type == TokenType.PUNCTUATION && t.value == ")" }
) : TokenValidator {

    override fun getExpectedDescription(): String {
        return "An expression with elements, operators, parentheses"
    }

    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        var consumed = listOf<Token>()
        var pos = position
        var needElem = true
        var depth = 0

        while (statementBuffer.hasNext()) {
            val t = statementBuffer.lookahead(pos)
            if (softEnd(t, needElem, depth)) break
            if (needElem) {
                when (val r = consumeElementOrGroup(statementBuffer, pos, consumed, depth)) {
                    is Step.Fail -> return r.err
                    is Step.Advanced -> { pos = r.pos; consumed = r.consumed; needElem = r.needElem; depth = r.depth }
                    is Step.Stop -> return ValidationResult.Error("Internal bug!", pos) // should not happen
                }
            } else {
                when (val r = afterElementStep(statementBuffer, pos, consumed, depth)) {
                    is Step.Fail -> return r.err
                    is Step.Stop -> break
                    is Step.Advanced -> { pos = r.pos; consumed = r.consumed; needElem = r.needElem; depth = r.depth }
                }
            }
        }
        return finalize(position, pos, consumed, needElem, depth)
    }

    // ---------- helpers ----------

    private fun softEnd(t: Token, needElem: Boolean, depth: Int): Boolean =
        !needElem && depth == 0 && !isOperator(t)

    private fun consumeElementOrGroup(buf: TokenBuffer, pos0: Int, cons: List<Token>, depth0: Int): Step {
        val t = buf.lookahead(pos0)
        return when {
            isElement(t) -> Step.Advanced(pos0 + 1, add(cons, t), needElem = false, depth = depth0)
            isGroupStart(t) -> Step.Advanced(pos0 + 1, add(cons, t), needElem = true, depth = depth0 + 1)
            else -> Step.Fail(err(pos0, "Expected element or '(', found '${t.value}'"))
        }
    }

    private fun afterElementStep(buf: TokenBuffer, pos0: Int, cons: List<Token>, depth0: Int): Step {
        val t = buf.lookahead(pos0)
        return when {
            isOperator(t) ->
                Step.Advanced(pos0 + 1, add(cons, t), needElem = true, depth = depth0)
            isGroupEnd(t) && depth0 > 0 ->
                Step.Advanced(pos0 + 1, add(cons, t), needElem = false, depth = depth0 - 1)
            isGroupEnd(t) && depth0 == 0 ->
                Step.Stop
            else ->
                if (depth0 == 0) {
                    Step.Stop
                } else {
                    Step.Fail(err(pos0, "Expected operator or ')', found '${t.value}'"))
                }
        }
    }

    private fun finalize(start: Int, pos: Int, cons: List<Token>, needElem: Boolean, depth: Int): ValidationResult {
        if (depth != 0) return err(pos, "Unmatched '('")
        if (needElem && pos > start) return err(pos, "Expression cannot end with operator")
        return ValidationResult.Success(cons)
    }

    // ---------- util ----------

    private fun err(p: Int, m: String) = ValidationResult.Error(m, p)

    private sealed class Step {
        data class Advanced(val pos: Int, val consumed: List<Token>, val needElem: Boolean, val depth: Int) : Step()
        data object Stop : Step()
        data class Fail(val err: ValidationResult.Error) : Step()
    }

    private fun add(list: List<Token>, t: Token): List<Token> {
        val newList = ArrayList<Token>(list.size + 1)
        newList.addAll(list)
        newList.add(t)
        return newList
    }
}
