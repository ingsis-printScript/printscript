package org.example.cli.util

import org.example.common.ErrorHandler

class CliErrorHandler : ErrorHandler {
    override fun handleError(message: String) {
        System.err.println("Error: $message")
        System.err.flush()
    }
}