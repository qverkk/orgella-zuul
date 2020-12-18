package com.qverkk.orgella.zuul.security

import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
class WebSecurity(private val env: Environment) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.headers().frameOptions().disable()
        http.authorizeRequests()
            .antMatchers(
                env.getProperty("api.users.actuator.url.path")
            ).permitAll()
            .antMatchers(
                env.getProperty("api.zuul.actuator.url.path")
            ).permitAll()
            .antMatchers(HttpMethod.POST, env.getProperty("api.login.url.path")).permitAll()
            .antMatchers(HttpMethod.POST, env.getProperty("api.registration.url.path")).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(AuthorizationFilter(authenticationManager(), env))

        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }
}