package no.ecm.eureka

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
//@EnableEurekaServer
@SpringBootApplication(scanBasePackages = ["no.ecm.euruka"])
class EurekaServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(EurekaServerApplication::class.java, *args)
}