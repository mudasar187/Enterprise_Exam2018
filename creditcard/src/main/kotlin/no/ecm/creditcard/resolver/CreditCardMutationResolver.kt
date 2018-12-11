package no.ecm.creditcard.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import graphql.execution.DataFetcherResult
import graphql.servlet.GenericGraphQLError
import no.ecm.creditcard.model.converter.CreditCardConverter
import no.ecm.creditcard.repository.CreditCardRepository
import no.ecm.utils.dto.creditCard.InputCreditCardDto
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.messages.InfoMessages
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.CrossOrigin

@Component
@CrossOrigin(origins = ["http://localhost:8080"])
class CreditCardMutationResolver(
        private var creditCardRepository: CreditCardRepository
): GraphQLMutationResolver {

    val logger = logger<CreditCardMutationResolver>()

    fun createCreditCard(input: InputCreditCardDto): DataFetcherResult<String> {
        val auth = SecurityContextHolder.getContext().authentication

        if (auth.name == input.username || isAdmin()) {
            when {
                input.cardNumber.isNullOrBlank() -> {
                    val errorMsg = ExceptionMessages.missingRequiredField("cardNumber")
                    logger.warn(errorMsg)
                    return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
                }
                input.cvc == null -> {
                    val errorMsg = ExceptionMessages.missingRequiredField("cvc")
                    logger.warn(errorMsg)
                    return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
                }
                input.expirationDate.isNullOrBlank() -> {
                    val errorMsg = ExceptionMessages.missingRequiredField("expirationDate")
                    logger.warn(errorMsg)
                    return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
                }
                input.username.isNullOrBlank() -> {
                    val errorMsg = ExceptionMessages.missingRequiredField("username")
                    logger.warn(errorMsg)
                    return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
                }

                creditCardRepository.existsByCreditcardNumber(input.cardNumber!!) -> {
                    val errorMsg = (ExceptionMessages.resourceAlreadyExists("CreditCard", "cardnumber", input.cardNumber!!))
                    logger.warn(errorMsg)
                    return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
                }

                else -> {

                    val id = creditCardRepository.save(CreditCardConverter.dtoToEntity(input)).id

                    val msg = InfoMessages.entityCreatedSuccessfully("CreditCard", id.toString())
                    logger.info(msg)

                    return DataFetcherResult(id.toString(), listOf())
                }
            }
        } else {
            val errorMsg = ExceptionMessages.unauthorizedUser(auth.name)
            logger.warn(errorMsg)
            return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
        }
    }

    fun deleteCreditCardById(inputId: String): DataFetcherResult<String> {
    
        val id = try {
            inputId.toLong()
        } catch (e: Exception) {
            val errorMsg: String =  ExceptionMessages.invalidIdParameter()
            return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
        }
    
        if (!creditCardRepository.existsById(id)) {
            val errorMsg = ExceptionMessages.notFoundMessage("CreditCard", "id", id.toString())
            logger.warn(errorMsg)
            return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
        }

        val dto = CreditCardConverter.entityToDto(creditCardRepository.findById(id).get())
        val auth = SecurityContextHolder.getContext().authentication

        return if(auth.name == dto.username || isAdmin()) {
            creditCardRepository.deleteById(id)

            val msg = InfoMessages.entitySuccessfullyDeleted("CreditCard", inputId)
            logger.info(msg)
            DataFetcherResult(msg, listOf())
        } else {
            val errorMsg = ExceptionMessages.unauthorizedUser(auth.name)
            logger.warn(errorMsg)
            DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
        }
    }

    fun isAdmin(): Boolean {
        val role = (SecurityContextHolder
                .getContext()
                .authentication
                .principal as UserDetails)
                .authorities
        return role.stream().anyMatch { e -> (e as GrantedAuthority).authority.contains("ROLE_ADMIN") }
    }
}