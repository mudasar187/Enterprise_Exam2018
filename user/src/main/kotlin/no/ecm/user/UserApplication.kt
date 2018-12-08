package no.ecm.user

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication(scanBasePackages = ["no.ecm.user"])
@EnableEurekaClient
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