package no.ecm.creditcard.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import no.ecm.creditcard.model.converter.CreditCardConverter
import no.ecm.creditcard.repository.CreditCardRepository
import no.ecm.utils.dto.creditCard.CreditCardDto
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class CreditCardQueryResolver(
        private var creditCardRepository: CreditCardRepository
): GraphQLQueryResolver {

    val logger = logger<CreditCardQueryResolver>()

    fun creditcardById(username: String): CreditCardDto? {

        if (!creditCardRepository.existsByUsername(username)) {
            val errorMsg = ExceptionMessages.notFoundMessage("CreditCard", "username", username)
            logger.warn(errorMsg)
            return null
        }

        val dto = CreditCardConverter.entityToDto(creditCardRepository.findByUsername(username))
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