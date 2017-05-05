package ru.giss.util.model.token;

/**
 * @author Ruslan Izmaylov
 */
public class Token {

    private int position;
    private String string;
    private TokenType type;

    public Token(int position, String string, TokenType type) {
        this.position = position;
        this.string = string;
        this.type = type;
    }

    public Token withType(TokenType newType) {
        return new Token(position, string, newType);
    }

    public int getPosition() {
        return position;
    }

    public String getString() {
        return string;
    }

    public TokenType getType() {
        return type;
    }

    public boolean isUndefined() {
        return type.isUndefined();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        return position == token.position;
    }

    @Override
    public int hashCode() {
        return position;
    }

    @Override
    public String toString() {
        return "Token{" +
                "position=" + position +
                ", string='" + string + '\'' +
                ", type=" + type +
                '}';
    }
}
