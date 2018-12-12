package no.ecm.creditcard

import no.ecm.creditcard.model.entity.CreditCard
import no.ecm.creditcard.repository.CreditCardRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@Profile("test")
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