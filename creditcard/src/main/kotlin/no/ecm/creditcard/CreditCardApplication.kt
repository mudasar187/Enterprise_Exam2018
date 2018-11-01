package no.ecm.creditcard

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["no.ecm.creditcard"])
class CreditCardApplication {}

/*
    UI accessible at
    http://localhost:8080/graphiql
    (note the "i" between graph and ql...)

    UI graph representation at
    http://localhost:8080/voyager

 */

fun main(args: Array<String>) {
    SpringApplication.run(CreditCardApplication::class.java, *args)
}