package org.example.lexer;

import org.example.common.tokens.Token;
import org.example.common.tokens.detectors.TokenDetector;
import org.example.common.tokens.tokenizer.Tokenizer;

import java.util.List;

public class Lexer {
    private final List<TokenDetector> detectors;

    public Lexer(List<TokenDetector> detectors) {
        this.detectors = detectors;
    }

    public List<Token> lex(String string){
        //funcion split -> genera inputs
        //funcion detect el type -> tokenDetector (devuelve el tokenizer a usar)
        //funcion tokenizer -> tokenizer (algo como una factory) -> devuelve el token acorde al input
        //poner el token en la lista
    }

    //nivel de dificultad = BAJO
    public List<String> split(String string){
        return List.of(string);
    }
    // EN split:
        // incluir separación de comas?
        // incluir separación de posibles iguales sin espacios...?

    //nivel de dificultad = ALTO
    public String detectToken(String string){}

    //nivel de dificultad = MEDIO
    public Token tokenizer(String string){}

}
