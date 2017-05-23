package ru.giss.search.parsing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.giss.AddressModel;
import ru.giss.util.model.address.Address;
import ru.giss.util.model.address.AddressFilter;

/**
 * @author Ruslan Izmaylov
 */
@Configuration
public class TestConfig {

    @Bean
    public AddressFilter getAddressFilter() {
        return address -> {
            if (address.getType() == AddressModel.AddressType.AT_COUNTRY) {
                return true;
            }
            Address cur = address;
            while (cur != null) {
                if (cur.getId() == Address.MSK_REGION || cur.getId() == Address.SPB_REGION) {
                    return true;
                }
                cur = cur.getParent();
            }
            return false;
        };
    }
}
