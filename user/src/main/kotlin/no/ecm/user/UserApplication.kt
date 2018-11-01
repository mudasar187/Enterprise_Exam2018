package no.ecm.user

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["no.ecm.user"])
class UserApplication {}

/*
    UI accessible at
    http://localhost:8081/graphiql
    (note the "i" between graph and ql...)

    UI graph representation at
    http://localhost:8081/voyager

 */

fun main(args: Array<String>) {
    SpringApplication.run(UserApplication::class.java, *args)
}