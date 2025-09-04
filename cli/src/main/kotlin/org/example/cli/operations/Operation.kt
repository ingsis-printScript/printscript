package org.example.cli.operations

import org.example.common.results.Result

interface Operation {
    fun execute() : Result
}
