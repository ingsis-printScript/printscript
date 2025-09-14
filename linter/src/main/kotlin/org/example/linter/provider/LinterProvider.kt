package org.example.linter.provider

import org.example.linter.Linter

interface LinterProvider {
    fun provide(): Linter
}
