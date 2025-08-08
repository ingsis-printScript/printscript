package org.example.common.tokens.tokenizer

import org.example.common.Range
import org.example.common.tokens.KeywordToken
import org.example.common.tokens.Keywords
import org.example.common.tokens.Token

class KeywordTokenizer: Tokenizer {
    override fun tokenize(string: String, range: Range): Token {
        //buscar la keyword y meterla en variable keyword si se quiere hacer "expansible"
        val keyword: Keywords = Keywords.fromString(string)
        val token: KeywordToken = KeywordToken(keyword, string, range)
        return token
    }

}