package org.example.formatter.rules

import org.example.common.configuration.RulesConfiguration
import org.example.formatter.FormatterContext
import org.example.token.Token

interface Rule {
    fun isEnabled(configuration: RulesConfiguration): Boolean = true

    fun before(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {}

    fun after(prev: Token?, cur: Token, next: Token?, ctx: FormatterContext) {}
}