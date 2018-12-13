package no.ecm.creditcard.repository

import no.ecm.creditcard.model.entity.CreditCard
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditCardRepository : CrudRepository<CreditCard, Long> {

    fun findByUsername(username: String): CreditCard
    fun existsByUsername(username: String): Boolean
    fun existsByUsernameOrCreditcardNumber(username: String, creditcardNumber: String): Boolean
}