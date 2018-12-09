//package no.ecm.creditcard.security
//
//import org.springframework.context.annotation.Configuration
//import org.springframework.http.HttpMethod
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
//import org.springframework.security.config.http.SessionCreationPolicy
//
//@Configuration
//@EnableWebSecurity
//class WebSecurityConfig : WebSecurityConfigurerAdapter() {
//
//
//    override fun configure(http: HttpSecurity) {
//        http
//                .httpBasic()
//                .and()
//                .authorizeRequests()
//
//                // Movies
//                .antMatchers(HttpMethod.GET,"/movies/**").authenticated()
//                .antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
//
//                .antMatchers(HttpMethod.GET,"/creditcards?{query}")
//                /*
//                    the "#" resolves the variable in the path, "{id}" in this case.
//                    the "@" resolves a current bean.
//                  */
//                .access("hasRole('USER') and @userSecurity.checkId(authentication, #query)")
//
//
//                .antMatchers("/**").hasRole("ADMIN")
//
//                .anyRequest().denyAll()
//                .and()
//                .csrf().disable()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
//    }
//}
//
//
