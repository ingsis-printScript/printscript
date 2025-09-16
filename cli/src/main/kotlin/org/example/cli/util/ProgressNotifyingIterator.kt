package org.example.cli.util

import org.example.common.ProgressObserver

class ProgressNotifyingIterator(
    private val delegate: Iterator<String>,
    private val reporter: ProgressObserver
) : Iterator<String> {
    init { reporter.onStart() }

    override fun hasNext(): Boolean = delegate.hasNext()

    override fun next(): String {
        val line = delegate.next()
        reporter.onAdvanceLine()
        return line
    }
}