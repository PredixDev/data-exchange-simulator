package com.ge.predix.solsvc.simulator.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by deepak on 6/19/18.
 */
@Configuration
@Order(1)
public class CustomSecurity extends WebSecurityConfigurerAdapter {

    @SuppressWarnings("nls")
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.antMatcher("/**")
                .authorizeRequests().anyRequest().permitAll();
        http.csrf().disable();
    }

}