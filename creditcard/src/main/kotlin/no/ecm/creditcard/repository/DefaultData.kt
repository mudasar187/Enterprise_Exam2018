package no.ecm.creditcard.repository

import no.ecm.creditcard.model.entity.CreditCard
import org.springframework.stereotype.Component
import java.time.LocalDate
import javax.annotation.PostConstruct

@Component
class DefaultData(
        private var creditCardRepository: CreditCardRepository
) {

    @PostConstruct
    fun createData(){

        val jondoeCreditCard = CreditCard(
                creditcardNumber = "6756675476575675674563645654",
                cvc = 123,
                expirationDate = "01/19",
                username = "jondoe")

        val foobarCreditCard = CreditCard(
                creditcardNumber = "2346578654321345678954324567",
                cvc = 544,
                expirationDate = "06/20",
                username = "foobar"
        )
        creditCardRepository.saveAll(mutableListOf(jondoeCreditCard, foobarCreditCard))



    }
}