package no.ecm.cinema

import no.ecm.utils.exception.RestResponseEntityExceptionHandler
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.netflix.ribbon.RibbonClient
import org.springframework.web.bind.annotation.ControllerAdvice


@SpringBootApplication(scanBasePackages = ["no.ecm.cinema", "no.ecm.utils"])
@ControllerAdvice(basePackageClasses = [RestResponseEntityExceptionHandler::class])
@EnableEurekaClient
class CinemaApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(CinemaApplication::class.java, *args)
}