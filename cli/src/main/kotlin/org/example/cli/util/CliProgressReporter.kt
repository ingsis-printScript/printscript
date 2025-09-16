package org.example.cli.util

import org.example.common.ProgressObserver
import java.nio.file.Files
import java.nio.file.Path

class CliProgressReporter private constructor(
    private val totalSupplier: () -> Long?
) : ProgressObserver {

    private var total: Long? = 0
    private var current: Long = 0
    private var lastPct: Int = -1

    override fun onStart() {
        total = totalSupplier()
        current = 0
        lastPct = -1
        total?.let { System.err.printf("Progress: 0/%d (0%%)%n", it) }
    }

    override fun onAdvanceLine() {
        current++
        val t = total
        if (t == null) {
            System.err.printf("Processed lines: %d%n", current)
            System.err.flush()
            return
        }
        val pct = ((current * 100) / t).toInt().coerceAtMost(100)
        if (pct != lastPct) {
            System.err.printf("Progress: %d/%d (%d%%)%n", current, t, pct)
            System.err.flush()
            lastPct = pct
        }
    }

    companion object {
        fun fromPath(path: Path): CliProgressReporter =
            CliProgressReporter {
                Files.newBufferedReader(path).use { br ->
                    var c = 0L
                    while (br.readLine() != null) c++
                    c
                }
            }

        fun fromFixedTotal(total: Long): CliProgressReporter =
            CliProgressReporter { total }

        fun unknownTotal(): CliProgressReporter =
            CliProgressReporter { null }
    }
}
