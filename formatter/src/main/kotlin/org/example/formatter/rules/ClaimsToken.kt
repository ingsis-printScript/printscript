package org.example.formatter.rules

import org.example.common.configuration.RulesConfiguration
import org.example.token.Token

interface ClaimsToken {
    fun claims(prev: Token?, cur: Token?, next: Token?, cfg: RulesConfiguration): Boolean
}
