package org.example.formatter

import org.example.common.enums.Operator
import org.example.common.configuration.RulesConfiguration
import org.example.token.Token
import org.example.token.TokenType
import java.io.Writer

class FormatterContext(
    val writer: Writer,
    val configuration: RulesConfiguration
) {
    private var lastWasSpace = false
    private var lastWasNewline = false
    private var pendingSpaces = 0
    private var pendingNewlines = 0
    private var suppressNextSpace = false

    fun writeRaw(text: String) {
        writer.write(text)
        if (text.isNotEmpty()) {
            val c = text.last()
            lastWasSpace = (c == ' ')
            lastWasNewline = (c == '\n')
        }
    }

    fun spaceOnce() {
        if (suppressNextSpace) {
            suppressNextSpace = false
            return
        }
        if (pendingNewlines > 0) {
            return
        }
        pendingSpaces = maxOf(pendingSpaces, 1)
    }

    fun clearPendingSpaces() {
        pendingSpaces = 0
        suppressNextSpace = false
    }

    fun setPendingSpaces(n: Int) {
        if (suppressNextSpace) {
            suppressNextSpace = false
            pendingSpaces = 0
            return
        }
        pendingSpaces = if (n <= 0) 0 else 1
    }

    fun newlineOnce() {
        pendingNewlines += 1
        pendingSpaces = 0
        suppressNextSpace = false
    }

    fun requestNoNextSpace() {
        suppressNextSpace = true
        pendingSpaces = 0
    }


    fun flushPendingGap() {
        if (pendingNewlines > 0) {
            repeat(pendingNewlines) { writer.write("\n") }
            lastWasNewline = true
            lastWasSpace = false
        } else if (pendingSpaces > 0 && !lastWasSpace && !lastWasNewline) {
            writer.write(" ")
            lastWasSpace = true
            lastWasNewline = false
        }
        // reset
        pendingSpaces = 0
        pendingNewlines = 0
        suppressNextSpace = false
    }


    // Gaps por Position (preserva intención original pero a lo sumo 1 espacio)
    fun emitOriginalSpace(prev: Token?, cur: Token) {
        if (prev == null) return
        val gapLines = cur.position.line - prev.position.line
        if (gapLines > 0) {
            // preservamos los saltos de línea físicos
            pendingNewlines = maxOf(pendingNewlines, gapLines)
            // si el token actual no empieza en col 1, permitimos 1 espacio
            val gapSpaces = (cur.position.column - 1).coerceAtLeast(0)
            if (gapSpaces > 0) pendingSpaces = maxOf(pendingSpaces, 1)
            // cualquier suppress se respeta al flushear mediante requestNoNextSpace()
        } else {
            val gapSpaces = (cur.position.column - (prev.position.column + prev.value.length)).coerceAtLeast(0)
            if (gapSpaces > 0) pendingSpaces = maxOf(pendingSpaces, 1)
        }
    }
}
