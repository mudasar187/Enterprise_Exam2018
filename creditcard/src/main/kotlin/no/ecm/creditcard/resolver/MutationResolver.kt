package no.ecm.creditcard.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.google.common.base.Throwables
import graphql.execution.DataFetcherResult
import graphql.servlet.GenericGraphQLError
import no.ecm.creditcard.model.converter.CreditCardConverter
import no.ecm.creditcard.repository.CreditCardRepository
import no.ecm.utils.dto.creditCard.InputCreditCardDto
import org.springframework.stereotype.Component
import javax.validation.ConstraintViolationException

@Component
class MutationResolver(
        private var creditCardRepository: CreditCardRepository
): GraphQLMutationResolver {


    fun createCreditCard(input: InputCreditCardDto): DataFetcherResult<String> {

        val id = try {
            creditCardRepository.save(CreditCardConverter.dtoToEntity(input)).id
        } catch (e: Exception) {
            val cause = Throwables.getRootCause(e)
            val msg = if (cause is ConstraintViolationException) {
                "Violated constraints: ${cause.message}"
            } else {
				"${e.javaClass}: ${e.message}"
            }
            return DataFetcherResult<String>(null, listOf(GenericGraphQLError(msg)))
        }

        return DataFetcherResult(id.toString(), listOf())

    }

    fun deleteCreditCardById(inputId: String): Boolean {

        val id: Long
        try {
            id = inputId.toLong()
        } catch (e: Exception){
            return false
        }

        if (!creditCardRepository.existsById(id)) {
            return false
        }

        creditCardRepository.deleteById(id)

        return true
    }

}