package no.ecm.movie.security

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

                // Movies
                .antMatchers(HttpMethod.GET,"/movies/**").permitAll()
                .antMatchers(HttpMethod.PATCH, "/movies/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/movies/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/movies/{id}").hasRole("ADMIN")

                //TODO bruke course Now PLaying
                .antMatchers(HttpMethod.GET,"/now-playings/**").permitAll()
                .antMatchers(HttpMethod.PATCH, "/now-playings/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/now-playings/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/now-playings/{id}").hasRole("ADMIN")

                //Genres
                .antMatchers(HttpMethod.GET,"/genres/**").permitAll()
                .antMatchers(HttpMethod.PATCH, "/genres/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/genres/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/genres/{id}").hasRole("ADMIN")

                .anyRequest().denyAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }
}