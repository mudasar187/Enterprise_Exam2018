package no.ecm.order

import no.ecm.utils.exception.RestResponseEntityExceptionHandler
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.web.bind.annotation.ControllerAdvice

@SpringBootApplication(scanBasePackages = ["no.ecm.order", "no.ecm.utils"])
@ControllerAdvice(basePackageClasses = [RestResponseEntityExceptionHandler::class])
@EnableEurekaClient
class OrderApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(OrderApplication::class.java, *args)
}