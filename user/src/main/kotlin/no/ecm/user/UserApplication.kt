package no.ecm.user

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["no.ecm.user"])
class UserApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(UserApplication::class.java, *args)
}