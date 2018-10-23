package no.ecm.order

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["no.ecm.order"])
class OrderApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(OrderApplication::class.java, *args)
}