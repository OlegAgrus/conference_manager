package com.agrus.conference_manager.security;

import com.agrus.conference_manager.service.MongoDBUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private MongoDBUserDetailsService mongoDBUserDetailsService;

    @Autowired
    public SecurityConfiguration(MongoDBUserDetailsService mongoDBUserDetailsService) {
        this.mongoDBUserDetailsService = mongoDBUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and().formLogin()
                .and().logout()
                .and().sessionManagement().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(mongoDBUserDetailsService)
                .passwordEncoder(passwordEncoder());
        authenticationManagerBuilder.inMemoryAuthentication()
                .withUser("user").password(passwordEncoder().encode("user")).roles("USER");
    }

}
