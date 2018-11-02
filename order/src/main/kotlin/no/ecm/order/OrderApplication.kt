package no.ecm.order

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication(scanBasePackages = ["no.ecm.order"])
@EnableEurekaClient
class OrderApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(OrderApplication::class.java, *args)
}