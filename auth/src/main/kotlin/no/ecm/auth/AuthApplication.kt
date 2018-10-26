package no.ecm.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["no.ecm.auth"])
class AdminApplication{}

fun main(args: Array<String>) {
	runApplication<AdminApplication>(*args)
}