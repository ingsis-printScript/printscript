package org.example.lexer.exceptions

class NoMoreTokensAvailableException(
    message: String = "No more tokens available"
) : Exception(message)
