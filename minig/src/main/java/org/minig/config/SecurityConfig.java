package org.minig.config;

import org.minig.MailAuthentication;
import org.minig.security.ApiAuthenticationEntryPoint;
import org.minig.security.MailAuthenticationProvider;
import org.minig.security.SecurityContextMailAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Kamill Sokol
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public MailAuthentication mailAuthentication() {
        return new SecurityContextMailAuthentication();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .eraseCredentials(false)
                .authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception  {
        webSecurity
                .ignoring()
                .antMatchers("/login", "/images/**", "/css/**", "/js/**");
    }

    @Order(1)
    @Configuration
    public static class ApiWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .eraseCredentials(false);
        }

        @Override
        public void configure(final HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**")
                    .authorizeRequests().anyRequest().fullyAuthenticated()
                    .and()
                    .httpBasic().authenticationEntryPoint(new ApiAuthenticationEntryPoint())
                    .and().csrf().disable();
        }
    }

    @Order(2)
    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .eraseCredentials(false);
        }

        @Override
        public void configure(final HttpSecurity http) throws Exception {
            http
                .antMatcher("/**")
                .authorizeRequests().anyRequest().hasAnyRole("USER")
                    .and()
                .formLogin()
                .loginPage("/login")
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
