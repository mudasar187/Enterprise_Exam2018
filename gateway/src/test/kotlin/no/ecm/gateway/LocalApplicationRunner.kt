package no.ecm.gateway

import org.springframework.boot.SpringApplication

class LocalApplicationRunner{}

fun main(args: Array<String>) {
    SpringApplication.run(GatewayApplication::class.java, "--spring.profiles.active=test")
}