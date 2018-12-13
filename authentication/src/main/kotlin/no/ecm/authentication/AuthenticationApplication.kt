package no.ecm.authentication

import no.ecm.utils.exception.RestResponseEntityExceptionHandler
import org.springframework.amqp.core.DirectExchange
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.ControllerAdvice

@SpringBootApplication(scanBasePackages = ["no.ecm.authentication", "no.ecm.utils.exception"])
@ControllerAdvice(basePackageClasses = [RestResponseEntityExceptionHandler::class])
@EnableEurekaClient
class AuthenticationApplication{

    @Bean
    fun passwordEncoder() : PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun direct(): DirectExchange {
        return DirectExchange("user-registration.direct")
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(AuthenticationApplication::class.java, *args)
}