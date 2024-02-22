package org.scriptio.parser;

import java.util.LinkedList;

import org.scriptio.parser.Nodes.*;

public class Parser {
    LinkedList<Token> tokens;

    int currIndex;
    Token curr;

    public Parser(LinkedList<Token> tokens) {
        this.tokens = tokens;

        if (!tokens.isEmpty()) {
            currIndex = 0;
            curr = tokens.get(currIndex);
        }
    }

    private Token next() {
        Token last = curr;

        currIndex++;

        if (currIndex < tokens.size()) {
            curr = tokens.get(currIndex);
        }

        return last;
    }

    public Program parse() throws Exception {
        Program program = new Program();

        while (currIndex < tokens.size()) {
            program.body.add(parseStatement());
        }

        return program;
    }

    private Node parseStatement() throws Exception {
        return switch (curr.type) {
            case VariableDeclaration -> parseVariableDeclaration();
            default -> parseAdditiveExpression();
        };
    }

    private Node parseVariableDeclaration() throws Exception {
        next();

        Token type = next();

        if (type == null || type.type != Token.TokenTypes.Type) {
            throw new Exception("Expected type following fac!");
        }

        Token id = next();

        if (id == null || id.type != Token.TokenTypes.Identifier) {
            throw new Exception("Expected identifier following type!");
        }

        Token next = next();

        if (next == null) {
            throw new Exception("Expected variable assignment or semicolon following identifier!");
        }

        if (next.type == Token.TokenTypes.SemiColon) {
            return new VariableDeclaration(new VariableDeclarator(new Identifier(id.value), null));
        }

        if (next.type != Token.TokenTypes.Equals) {
            throw new Exception("Expected variable assignment following identifier!");
        }

        Token initToken = next();
        Node init;

        if (initToken == null) {
            throw new Exception("Expected value for variable declarator!");
        }

        switch (type.value) {
            case "verbum" -> {
                if (initToken.type != Token.TokenTypes.String) {
                    throw new Exception("Expected verbum!");
                }

                init = new Literal(initToken.value);
                break;
            }
            case "veredictumne" -> {
                if (initToken.type != Token.TokenTypes.Boolean) {
                    throw new Exception("Expected veredictumne!");
                }

                init = new Literal(initToken.value);
                break;
            }
            case "numerus" -> {
                if (initToken.type == Token.TokenTypes.String || initToken.type == Token.TokenTypes.Boolean) {
                    throw new Exception("Expected numerus!");
                }

                init = parseAdditiveExpression();
                break;
            }
            default -> {
                init = parseAdditiveExpression();
                break;
            }
        }

        Token semicolon = next();

        if (semicolon == null || semicolon.type != Token.TokenTypes.SemiColon) {
            throw new Exception("Expected semicolon following declarator!");
        }

        return new VariableDeclaration(new VariableDeclarator(new Identifier(id.value), (Literal) init));
    }

    private Node parseAdditiveExpression() throws Exception {
        Node left = parseMultiplicativeExpression();

        while (curr.value.equals("+") || curr.value.equals("-")) {
            String operator = next().value;
            Node right = parseMultiplicativeExpression();

            left = new BinaryExpression(operator, (Literal) left, (Literal) right);
        }

        return left;
    }

    private Node parseMultiplicativeExpression() throws Exception {
        Node left = parsePrimaryExpression();

        while (curr.value.equals("*") || curr.value.equals("/")) {
            String operator = next().value;
            Node right = parsePrimaryExpression();

            left = new BinaryExpression(operator, (Literal) left, (Literal) right);
        }

        return left;
    }

    private Node parsePrimaryExpression() throws Exception {
        return switch (curr.type) {
            case Identifier -> new Identifier(next().value);
            case String, Number, Boolean, Null -> new Literal(next().value);
            case OpenParen -> {
                next();

                Node innerNode = parseAdditiveExpression();

                Token last = next();

                if (last == null || last.type != Token.TokenTypes.CloseParen) {
                    throw new Exception("Expected corresponding closing parenthesis!");
                }

                yield innerNode;
            }
            default -> throw new Exception("Unexpected token!");
        };
    }
}
