package no.ecm.cinema

import org.springframework.boot.SpringApplication

class LocalApplicationRunner{}

fun main(args: Array<String>) {
    SpringApplication.run(CinemaApplication::class.java, "--spring.profiles.active=test")
}