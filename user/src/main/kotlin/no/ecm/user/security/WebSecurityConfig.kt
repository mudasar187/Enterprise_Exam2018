package no.ecm.user.security

import no.ecm.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.cors.CorsUtils

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {



    override fun configure(http: HttpSecurity) {
        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/graphql/**")
                /*
                    the "#" resolves the variable in the path, "{id}" in this case.
                    the "@" resolves a current bean.
                  */
                .access("hasRole('USER') and @userSecurity.checkId(authentication)")
                .antMatchers(HttpMethod.POST, "/graphql/**").authenticated()
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


/**
 * Custom check. Not only we need a user authenticated, but we also
 * need to make sure that a user can only access his/her data, and not the
 * one of the other users
 */
class UserSecurity{

    @Autowired
    private lateinit var userRepository: UserRepository

    fun checkId(authentication: Authentication) : Boolean{
        val current = (authentication.principal as UserDetails).username


        return try {
            userRepository.findById(current).get().username == current
        } catch (e : Exception){
            false
        }
    }
}