package no.ecm.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author Christian Marker on 23/10/2018 at 14:49.
 */
@SpringBootApplication(scanBasePackages = ["no.ecm.admin"])
class AdminApplication{}

fun main(args: Array<String>) {
	runApplication<AdminApplication>(*args)
}