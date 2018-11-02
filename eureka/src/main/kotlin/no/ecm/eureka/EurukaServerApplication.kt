package no.ecm.eureka

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication(scanBasePackages = ["no.ecm.euruka"])
@EnableEurekaServer
class EurekaServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(EurekaServerApplication::class.java, *args)
}