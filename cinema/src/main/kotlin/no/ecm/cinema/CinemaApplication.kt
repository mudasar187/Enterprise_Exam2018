package no.ecm.cinema

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author Christian Marker on 23/10/2018 at 15:21.
 */
@SpringBootApplication(scanBasePackages = ["no.ecm.cinema"])
class CinemaApplication{}

fun main(args: Array<String>) {
	runApplication<CinemaApplication>(*args)
}