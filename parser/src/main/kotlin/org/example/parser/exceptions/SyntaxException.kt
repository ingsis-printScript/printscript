package org.example.parser.exceptions

class SyntaxException(
    message: String
) : Exception(message)

fun errorAt(message: String, position: Int): SyntaxException {
    return SyntaxException("Error in statement: $message at index $position")
}
