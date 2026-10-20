package com.lightshell.oauth2;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 以 war 部署到外部 Tomcat 10.1+ 时的引导类。
 */
public class ServletInitializer extends SpringBootServletInitializer {

    public ServletInitializer() {
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ResourceServerApplication.class);
    }

}
