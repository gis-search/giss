package ru.giss.search.parsing;

import org.pcollections.PVector;
import ru.giss.util.model.address.Address;
import ru.giss.util.model.token.Token;

/**
 * @author Ruslan Izmaylov
 */
public class ParseResult {

    private Address address;
    private long score;
    private PVector<Token> tokens;

    public ParseResult(Address address, long score, PVector<Token> tokens) {
        this.address = address;
        this.score = score;
        this.tokens = tokens;
    }

    public Address getAddress() {
        return address;
    }

    public long getScore() {
        return score;
    }

    public PVector<Token> getTokens() {
        return tokens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParseResult that = (ParseResult) o;

        return address != null ? address.equals(that.address) : that.address == null;
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }
}
