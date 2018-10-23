package no.ecm.movie

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = arrayOf("no.ecm.movie"))
class MovieApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(MovieApplication::class.java, *args)
}