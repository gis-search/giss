package ru.giss.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import ru.giss.web.config.WebConfig;

public class Bootstrap extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class[] getRootConfigClasses() {
        return new Class[]{RootConfig.class};
    }

    @Override
    protected Class[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
