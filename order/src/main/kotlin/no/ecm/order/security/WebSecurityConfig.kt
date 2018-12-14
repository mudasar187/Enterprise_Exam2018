package no.ecm.order.security

import no.ecm.order.service.InvoiceService
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

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {
        http.cors().and()
                .httpBasic()
                .and()
                .authorizeRequests()

                // Coupons
                .antMatchers(HttpMethod.GET,"/coupons/**").permitAll()
                .antMatchers(HttpMethod.PATCH, "/coupons/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/coupons/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/coupons").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/coupons/{id}").hasRole("ADMIN")

                // Invoices
                .antMatchers(HttpMethod.GET,"/invoices").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET,"/invoices/{id}/**")
                /*
                    the "#" resolves the variable in the path, "{id}" in this case.
                    the "@" resolves a current bean.
                  */
                .access("hasRole('USER') and @userSecurity.checkId(authentication, #id)")

                .antMatchers(HttpMethod.POST, "/invoices").authenticated()
                .antMatchers(HttpMethod.DELETE, "/invoices/{id}").hasRole("ADMIN")

                //Tickets
                .antMatchers(HttpMethod.GET,"/tickets/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/tickets/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/tickets/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/tickets").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/tickets/{id}").hasRole("ADMIN")

                .antMatchers("/swagger-resources/**").hasRole("ADMIN")
                .antMatchers("/swagger-ui.html").hasRole("ADMIN")
                .antMatchers("/v2/api-docs").hasRole("ADMIN")
                .antMatchers("/webjars/**").hasRole("ADMIN")

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
    private lateinit var invoiceService : InvoiceService

    fun checkId(authentication: Authentication, id: String) : Boolean{
        val current = (authentication.principal as UserDetails).username

        return invoiceService.findById(id).username == current
    }
}