package ru.giss.util.model.token;

/**
 * @author Ruslan Izmaylov
 */
public class UndefinedToken extends TokenType {

    public final static UndefinedToken INSTANCE = new UndefinedToken();

    private UndefinedToken() {
    }

    @Override
    public boolean isUndefined() {
        return true;
    }

    @Override
    public String toString() {
        return "UndefinedToken";
    }
}
