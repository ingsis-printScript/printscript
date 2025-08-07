package org.example.common.tokens.detectors;

import org.example.common.tokens.tokenizer.Tokenizer;

public interface TokenDetector {
    Tokenizer detect(String string);

}
