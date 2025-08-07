package org.example.common.tokens.tokenizer;

import org.example.common.tokens.Token;

public interface Tokenizer {
    Token tokenize(String string);
}
