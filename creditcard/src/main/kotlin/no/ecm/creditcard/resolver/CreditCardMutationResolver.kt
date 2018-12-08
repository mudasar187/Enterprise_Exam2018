package no.ecm.creditcard.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.google.common.base.Throwables
import graphql.execution.DataFetcherResult
import graphql.servlet.GenericGraphQLError
import no.ecm.creditcard.model.converter.CreditCardConverter
import no.ecm.creditcard.repository.CreditCardRepository
import no.ecm.utils.dto.creditCard.InputCreditCardDto
import no.ecm.utils.exception.ConflictException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.messages.InfoMessages
import no.ecm.utils.validation.ValidationHandler
import org.springframework.stereotype.Component
import javax.validation.ConstraintViolationException

@Component
class CreditCardMutationResolver(
        private var creditCardRepository: CreditCardRepository
): GraphQLMutationResolver {

    val logger = logger<CreditCardMutationResolver>()

    fun createCreditCard(input: InputCreditCardDto): DataFetcherResult<String> {
        
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

        creditCardRepository.deleteById(id)
    
        val msg = InfoMessages.entitySuccessfullyDeleted("CreditCard", inputId)
        logger.info(msg)
        return DataFetcherResult(msg, listOf())
    }

}