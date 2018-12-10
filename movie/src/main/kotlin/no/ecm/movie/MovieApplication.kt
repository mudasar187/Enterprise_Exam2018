package no.ecm.movie

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.web.bind.annotation.ControllerAdvice
import no.ecm.utils.exception.*
import org.springframework.cloud.netflix.ribbon.RibbonClient

@SpringBootApplication(scanBasePackages = ["no.ecm.movie", "no.ecm.utils"])
@ControllerAdvice(basePackageClasses = [RestResponseEntityExceptionHandler::class])
@EnableEurekaClient
@RibbonClient(name = "cinema-server")
class MovieApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(MovieApplication::class.java, *args)
}