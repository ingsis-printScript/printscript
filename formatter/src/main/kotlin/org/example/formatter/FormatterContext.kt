package org.example.formatter

import org.example.common.configuration.RulesConfiguration
import org.example.token.Token
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
    private var pendingIndentSpaces = 0

    fun writeRaw(text: String) {
        writer.write(text)
        if (text.isNotEmpty()) {
            val c = text.last()
            lastWasSpace = (c == ' ')
            lastWasNewline = (c == '\n')
        }
    }

    fun setPendingIndentSpaces(spaces: Int) {
        pendingIndentSpaces = spaces.coerceAtLeast(0)
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
        if (pendingNewlines > 0) {
            return
        }
        pendingSpaces = if (n <= 0) 0 else 1
    }

    fun requestNoNextSpace() {
        suppressNextSpace = true
        pendingSpaces = 0
    }

    fun setPendingNewlines(n: Int) {
        pendingNewlines = n
        if (pendingNewlines > 0) {
            pendingSpaces = 0
            suppressNextSpace = false
        }
    }

    fun flushPendingGap() {
        //mete saltos de linea
        if (pendingNewlines > 0) {
            repeat(pendingNewlines) { writer.write("\n") }
            lastWasNewline = true
            lastWasSpace = false
        }
        // Si estamos al inicio de línea y hay indent pendiente, escribimos la indentación para esa línea
        if ((pendingNewlines > 0 || lastWasNewline) && pendingIndentSpaces > 0) {
            repeat(pendingIndentSpaces) { writer.write(" ") }
            lastWasSpace = pendingIndentSpaces > 0
            lastWasNewline = false
        }
        //si no hay newLines, mete espacios
        if (pendingSpaces > 0 && !lastWasSpace && !lastWasNewline) {
            writer.write(" ")
            lastWasSpace = true
            lastWasNewline = false
        }
        // reset
        pendingSpaces = 0
        pendingNewlines = 0
        pendingIndentSpaces = 0
        suppressNextSpace = false
    }

    fun emitOriginalSpace(prev: Token?, cur: Token) {
        if (prev == null) return

        if (pendingNewlines > 0 || pendingSpaces > 0 || suppressNextSpace) return

        val gapLines = cur.position.line - prev.position.line
        if (gapLines > 0) {
            // preservamos los saltos de línea
            pendingNewlines = maxOf(pendingNewlines, gapLines)
            // si el token actual no empieza en la misma columna que el anterior, dejamos el espacio (si habia)
            val gapSpaces = (cur.position.column - 1).coerceAtLeast(0)
            if (gapSpaces > 0) pendingSpaces = maxOf(pendingSpaces, 1)
        } else { //gapLines == 0
            val gapSpaces = (cur.position.column - (prev.position.column + prev.value.length)).coerceAtLeast(0)
            if (gapSpaces > 0) pendingSpaces = maxOf(pendingSpaces, 1)
        }
    }
}
