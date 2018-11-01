package no.ecm.creditcard.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import no.ecm.creditcard.model.converter.CreditCardConverter
import no.ecm.creditcard.repository.CreditCardRepository
import no.ecm.utils.dto.creditCard.CreditCardDto
import org.springframework.stereotype.Component

@Component
class QueryResolver(
        private var creditCardRepository: CreditCardRepository
): GraphQLQueryResolver {


    fun creditcardById(inputId: String): CreditCardDto? {
        val id: Long
        try {
            id = inputId.toLong()
        } catch (e: Exception){
            return null
        }

        val creditCard = creditCardRepository.findById(id).orElse(null) ?: return null

        return CreditCardConverter.entityToDto(creditCard)
    }

}