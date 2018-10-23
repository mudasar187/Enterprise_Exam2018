package no.ecm.creditcard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author Christian Marker on 23/10/2018 at 15:35.
 */
@SpringBootApplication(scanBasePackages = ["no.ecm.creditcard"])
class CreditcardApplication{}

fun main(args: Array<String>) {
	runApplication<CreditcardApplication>(*args)
}