package no.ecm.creditcard.security

import org.springframework.context.annotation.Bean
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

                .antMatchers(HttpMethod.GET, "/creditcards/**")
                /*
                    the "#" resolves the variable in the path, "{id}" in this case.
                    the "@" resolves a current bean.
                  */
                .access("hasRole('USER') and @userSecurity.checkId(authentication)")
                .antMatchers(HttpMethod.POST, "/creditcards/**").authenticated()
                .antMatchers("/**").hasRole("ADMIN")

                .anyRequest().denyAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }

    @Bean
    fun userSecurity() : UserSecurity {
        return UserSecurity()
    }
}


