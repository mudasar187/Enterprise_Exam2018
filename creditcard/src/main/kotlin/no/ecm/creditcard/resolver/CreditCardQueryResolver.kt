package no.ecm.creditcard.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.execution.DataFetcherResult
import graphql.servlet.GenericGraphQLError
import no.ecm.creditcard.model.converter.CreditCardConverter
import no.ecm.creditcard.repository.CreditCardRepository
import no.ecm.utils.dto.creditCard.CreditCardDto
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.validation.ValidationHandler
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.CrossOrigin

@Component
@CrossOrigin(origins = ["http://localhost:8080"])
class CreditCardQueryResolver(
        private var creditCardRepository: CreditCardRepository
): GraphQLQueryResolver {

    val logger = logger<CreditCardQueryResolver>()

    fun creditcardById(inputId: String): CreditCardDto? {
        
        val id = try {
            inputId.toLong()
        } catch (e: Exception) {
            val errorMsg: String =  ExceptionMessages.invalidIdParameter()
            logger.warn(errorMsg)
            return null
        }

        if (!creditCardRepository.existsById(id)) {
            val errorMsg = ExceptionMessages.notFoundMessage("CreditCard", "id", inputId)
            logger.warn(errorMsg)
            return null
        }

        val dto = CreditCardConverter.entityToDto(creditCardRepository.findById(id).get())
        val auth = SecurityContextHolder.getContext().authentication

        return if(auth.name == dto.username || isAdmin()) {
            dto
        } else {
            logger.warn(ExceptionMessages.unauthorizedUser(auth.name))
            null
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