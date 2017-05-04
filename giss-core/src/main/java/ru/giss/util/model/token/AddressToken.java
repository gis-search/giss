package ru.giss.util.model.token;

import ru.giss.util.model.address.Address;

/**
 * @author Ruslan Izmaylov
 */
public class AddressToken extends TokenType {

    private Address address;

    public AddressToken(Address address) {
        this.address = address;
    }

    @Override
    public boolean isUndefined() {
        return false;
    }

    @Override
    public String toString() {
        return "AddressToken{" + address + '}';
    }
}
