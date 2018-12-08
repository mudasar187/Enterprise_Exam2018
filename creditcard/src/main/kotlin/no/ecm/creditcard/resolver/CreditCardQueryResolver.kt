package no.ecm.creditcard.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import no.ecm.creditcard.model.converter.CreditCardConverter
import no.ecm.creditcard.repository.CreditCardRepository
import no.ecm.utils.dto.creditCard.CreditCardDto
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.validation.ValidationHandler
import org.springframework.stereotype.Component

@Component
class CreditCardQueryResolver(
        private var creditCardRepository: CreditCardRepository
): GraphQLQueryResolver {

    val logger = logger<CreditCardQueryResolver>()

    fun creditcardById(inputId: String): CreditCardDto? {
        
        val id = ValidationHandler.validateId(inputId, "id")
    
        if (!creditCardRepository.existsById(id)) {
            val errorMsg = ExceptionMessages.notFoundMessage("CreditCard", "id", inputId)
            logger.warn(errorMsg)
            return null
        }

        return CreditCardConverter.entityToDto(creditCardRepository.findById(id).get())
    }

}