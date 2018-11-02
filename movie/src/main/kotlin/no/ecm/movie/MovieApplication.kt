package no.ecm.movie

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication(scanBasePackages = ["no.ecm.movie"])
@EnableEurekaClient
class MovieApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(MovieApplication::class.java, *args)
}