package ru.giss.util.model.token;

import ru.giss.AddressModel.AddressWord;

import java.util.List;

/**
 * @author Ruslan Izmaylov
 */
public class AddressWordToken extends TokenType {

    private List<AddressWord> words;

    public AddressWordToken(List<AddressWord> words) {
        this.words = words;
    }

    @Override
    public boolean isUndefined() {
        return false;
    }

    @Override
    public String toString() {
        return "AddressWordToken{" + words + '}';
    }
}
