package org.example.linter.provider

import org.example.common.ErrorHandler
import org.example.common.PrintScriptIterator
import org.example.common.results.Result
import org.example.linter.Linter
import java.io.InputStream

interface LinterProvider {
    fun provide(iterator: PrintScriptIterator<Result>, inputStream: InputStream, errorHandler: ErrorHandler): Linter
}
