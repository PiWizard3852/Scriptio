package org.scriptio.test;

import org.scriptio.parser.Lexer;
import org.scriptio.parser.Token;

import java.util.LinkedList;

public class LexerTest {
    public static void main(String[] args) throws Exception {
        String source = "fac variabilis veredictumne ge = verus; ge++;";

        Lexer lexer = new Lexer(source);
        LinkedList<Token> tokens = lexer.lex();

        System.out.println("[");

        for (Token token : tokens) {
            System.out.println("\t{");
            System.out.print("\t\ttype: ");
            System.out.println(token.type);
            System.out.print("\t\tvalue: ");
            System.out.println(token.value);
            System.out.println("\t},");
        }

        System.out.println("]");
    }
}
