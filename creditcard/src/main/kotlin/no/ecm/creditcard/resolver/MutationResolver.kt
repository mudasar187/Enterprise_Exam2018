package no.ecm.creditcard.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.google.common.base.Throwables
import no.ecm.creditcard.model.converter.CreditCardConverter
import no.ecm.creditcard.repository.CreditCardRepository
import no.ecm.utils.dto.creditCard.CreditCardDto
import no.ecm.utils.dto.creditCard.InputCreditCardDto
import org.springframework.stereotype.Component
import javax.validation.ConstraintViolationException

@Component
class MutationResolver(
        private var creditCardRepository: CreditCardRepository
): GraphQLMutationResolver {


    fun createCreditCard(input: InputCreditCardDto): CreditCardDto? {

        val creditCard = try {
            creditCardRepository.save(CreditCardConverter.dtoToEntity(input))
        } catch (e: Exception) {
            val cause = Throwables.getRootCause(e)
            val msg = if (cause is ConstraintViolationException) {
                "Violated constraints: ${cause.message}"
            }else {
                e.message
            }
            return null
        }

        println("id here: ${creditCard.username}")

        return CreditCardConverter.entityToDto(creditCard)

    }

}