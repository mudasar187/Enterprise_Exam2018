package no.ecm.cinema

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient


@SpringBootApplication(scanBasePackages = ["no.ecm.cinema"])
@EnableEurekaClient
class CinemaApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(CinemaApplication::class.java, *args)
}