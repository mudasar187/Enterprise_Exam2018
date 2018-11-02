package no.ecm.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient


@SpringBootApplication(scanBasePackages = ["no.ecm.auth"])
@EnableEurekaClient
class AdminApplication{}

fun main(args: Array<String>) {
	runApplication<AdminApplication>(*args)
}