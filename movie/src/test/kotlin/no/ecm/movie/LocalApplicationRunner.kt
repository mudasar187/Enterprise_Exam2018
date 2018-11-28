package no.ecm.movie

import org.springframework.boot.SpringApplication

class LocalApplicationRunner {}

fun main(args: Array<String>) {
    SpringApplication.run(MovieApplication::class.java, "--spring.profiles.active=test")
}