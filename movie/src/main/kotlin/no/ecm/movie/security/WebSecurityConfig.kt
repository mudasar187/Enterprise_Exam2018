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
                .antMatchers(HttpMethod.POST, "/movies").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/movies/{id}").hasRole("ADMIN")

                .antMatchers(HttpMethod.GET,"/now-playings/**").permitAll()

                /*
                    FIXME WARNING: Here we had to permit all patch requests due to the fact that RestTemplate strips the request Cookies,
                     -- so the user is no longer authenticated when OrderService makes a PATCH request to the MovieService endpoint:
                     -- /now-playings/{id}.
                */
                .antMatchers(HttpMethod.PATCH, "/now-playings/{id}").permitAll()
                .antMatchers(HttpMethod.PUT, "/now-playings/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/now-playings").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/now-playings/{id}").hasRole("ADMIN")

                //Genres
                .antMatchers(HttpMethod.GET,"/genres/**").permitAll()
                .antMatchers(HttpMethod.PATCH, "/genres/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/genres/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/genres").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/genres/{id}").hasRole("ADMIN")

                // Swagger
                .antMatchers("/swagger-resources/**").hasRole("ADMIN")
                .antMatchers("/swagger-ui.html").hasRole("ADMIN")
                .antMatchers("/v2/api-docs").hasRole("ADMIN")
                .antMatchers("/webjars/**").hasRole("ADMIN")

                .anyRequest().denyAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
    }
}