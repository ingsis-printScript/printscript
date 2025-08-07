package org.example.common.tokens;

public enum Keywords {
    VAR, LET, CONST, CONSOLE, LOG
}

// Por el momento solo hay let.
// DUDA: ¿incluir string y number como keywords?
// ¿Si no aislar let a "DeclaratorToken"? Problema con límite de extensibilidad?
// If so, ¿qué pasa con Keywords...?

