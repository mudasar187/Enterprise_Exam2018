package no.ecm.creditcard

import org.springframework.boot.SpringApplication

class LocalApplicationRunner{}

fun main(args: Array<String>) {
    SpringApplication.run(CreditCardApplication::class.java, "--spring.profiles.active=test")
}