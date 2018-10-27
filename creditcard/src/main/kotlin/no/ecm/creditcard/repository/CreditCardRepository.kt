package no.ecm.creditcard.repository

import no.ecm.creditcard.model.entity.CreditCard
import org.springframework.stereotype.Repository

@Repository
interface CreditCardRepository {

    fun findByUsername(username: String): CreditCard
}