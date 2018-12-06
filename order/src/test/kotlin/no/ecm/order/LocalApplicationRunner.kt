package no.ecm.order

import org.springframework.boot.SpringApplication

class LocalApplicationRunner {}

fun main(args: Array<String>) {
    SpringApplication.run(OrderApplication::class.java, "--spring.profiles.active=test")
}