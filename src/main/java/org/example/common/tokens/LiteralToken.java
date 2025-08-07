package org.example.common.tokens;

import org.example.common.Range;

public class LiteralToken<T> implements Token {
    String type;
    String raw;
    T value;
    Range range;
}
