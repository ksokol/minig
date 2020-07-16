package org.minig.config;

import org.minig.security.ApiAuthenticationEntryPoint;
import org.minig.security.MailAuthentication;
import org.minig.security.MailAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * @author Kamill Sokol
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public MailAuthentication mailAuthentication() {
        return new MailAuthentication();
    }

    @Autowired
    protected void configureGlobal(final AuthenticationManagerBuilder auth) {
        auth.eraseCredentials(false)
            .authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowUrlEncodedSlash(true);
        webSecurity.httpFirewall(firewall);

        webSecurity
                .ignoring()
                .antMatchers("/images/**", "/app/**", "/js/**", "/static/**", "/bower_components/**", "/css/**");
    }

    @Order(1)
    @Configuration
    public static class ApiMessageHtmlWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(final HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/*/message/*/html")
                    .authorizeRequests().anyRequest().fullyAuthenticated()
                    .and()
                    .httpBasic().authenticationEntryPoint(new ApiAuthenticationEntryPoint())
                    .and().csrf().disable()
                    .headers().frameOptions().disable().contentSecurityPolicy("script-src 'self'");
        }
    }

    @Order(2)
    @Configuration
    public static class ApiWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(final HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**")
                    .authorizeRequests().anyRequest().fullyAuthenticated()
                    .and()
                    .httpBasic().authenticationEntryPoint(new ApiAuthenticationEntryPoint())
                    .and().csrf().disable()
                    .headers().contentSecurityPolicy("script-src 'self'");
        }
    }

    @Order(3)
    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(final HttpSecurity http) throws Exception {
            http
                .antMatcher("/**")
                .authorizeRequests().anyRequest().hasAnyRole("USER")
                    .and()
                .formLogin()
                .loginPage("/login").permitAll()
                    .loginProcessingUrl("/check")
                    .defaultSuccessUrl("/", true)
                .failureUrl("/login?login=failed")
                .and()
                    .logout().logoutSuccessUrl("/login")
                    .and()
                    .csrf().disable()
                    .authenticationProvider(authenticationProvider());
        }
    }

    private static AuthenticationProvider authenticationProvider() {
        return new MailAuthenticationProvider();
    }

}
