package no.ecm.creditcard.repository

import no.ecm.creditcard.model.entity.CreditCard
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DefaultData(
        private var creditCardRepository: CreditCardRepository
) {

    //TODO implement after pull form Marker
    @PostConstruct
    fun createData(){

        //val creditCard = CreditCard(creditcardNumber = "43567865432", )
    }
}