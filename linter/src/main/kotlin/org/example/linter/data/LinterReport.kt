package org.example.linter.data

data class LinterReport(
    val violations: List<LinterViolation>,
){
    fun hasViolations(): Boolean = violations.isNotEmpty()
}