package org.scriptio.runtime;

import org.scriptio.parser.AST.Parser;
import org.scriptio.parser.Lexer;
import org.scriptio.parser.Token;

import java.util.LinkedList;

public class Interpreter {
    String source;

    Lexer lexer;
    Parser abstractSyntaxTree;

    LinkedList<Token> tokens;

    public Interpreter(String source) {
        this.source = source;
    }

    public void run() throws Exception {
        lexer = new Lexer(source);
        tokens = lexer.lex();

        abstractSyntaxTree = new Parser();

        for (Token token : tokens) {
            System.out.println(token.type);
            System.out.println(token.value + "\n");
        }
    }
}