package org.example.linter.provider

import org.example.linter.Linter

interface Provider {
    fun provide(): Linter
}
