package org.example.common.enums.keywords

import org.example.common.tokens.Keyword

enum class ConditionalKeyword(override val value: String) : Keyword {
    IF("if"),
    ELSE("else");
}
