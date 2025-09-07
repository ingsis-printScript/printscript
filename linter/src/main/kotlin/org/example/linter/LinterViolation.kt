package org.example.linter

import org.example.common.Range

data class LinterViolation(
    val message: String,
    val range: Range
) {
}