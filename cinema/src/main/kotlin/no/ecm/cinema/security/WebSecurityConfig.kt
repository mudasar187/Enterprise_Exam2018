package no.ecm.cinema.security

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {

        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/cinemas/**").permitAll()
                .antMatchers(HttpMethod.PATCH, "/cinemas/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/cinemas/{id}/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/cinemas/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/cinemas/{id}/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/cinemas/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/cinemas/{id}/**").hasRole("ADMIN")
                .anyRequest().denyAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }
}

