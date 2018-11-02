package no.ecm.cinema

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication(scanBasePackages = ["no.ecm.cinema"])

class CinemaApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(CinemaApplication::class.java, *args)
}