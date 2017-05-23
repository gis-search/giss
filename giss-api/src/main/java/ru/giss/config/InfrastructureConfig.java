package ru.giss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.giss.util.model.address.AddressFilter;

/**
 * @author Ruslan Izmaylov
 */
@Configuration
public class InfrastructureConfig {

    @Bean
    public AddressFilter getAddressFilter() {
        return address -> true;
    }
}
