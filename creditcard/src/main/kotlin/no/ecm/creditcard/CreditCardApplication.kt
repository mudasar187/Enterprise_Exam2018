package no.ecm.creditcard

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["no.ecm.creditcard"])
class CreditCardApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(CreditCardApplication::class.java, *args)
}