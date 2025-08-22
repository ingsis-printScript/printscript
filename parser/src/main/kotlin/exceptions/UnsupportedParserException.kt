package org.example.parser.exceptions

import org.example.common.Range

class UnsupportedParserException(
    message: String,

) : Exception("$message")