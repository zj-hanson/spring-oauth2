package com.lightshell.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;

/**
 * @author KevinDong
 */
@Configuration
public class NacosConfiguration implements ApplicationRunner {

    @Autowired(required = false)
    private NacosAutoServiceRegistration registration;

    @Override
    public void run(ApplicationArguments args) {
        if (registration != null) {
            registration.start();
        }
    }

}
