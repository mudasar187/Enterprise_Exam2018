package no.ecm.user.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import no.ecm.user.model.converter.UserConverter
import no.ecm.user.repository.UserRepository
import no.ecm.utils.dto.user.UserDto
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails


/* INFO:

    Due to the limitations of compatibility between GraphQL and Spring Security, is the authorization check to data done here in this file
    instead of WebSecurityConfig.

 */

@Component
class UserQueryResolver(
        private var userRepository: UserRepository
): GraphQLQueryResolver {
    
    val logger = logger<UserQueryResolver>()

    fun isAdmin(): Boolean {
        val role = (SecurityContextHolder
                .getContext()
                .authentication
                .principal as UserDetails)
                .authorities
        return role.stream().anyMatch { e -> (e as GrantedAuthority).authority.contains("ROLE_ADMIN") }
    }

    fun userById(inputId: String): UserDto? {

        val auth = SecurityContextHolder.getContext().authentication

        if(auth.name == inputId || isAdmin()){
            if (!userRepository.existsById(inputId)) {
                val errorMsg = ExceptionMessages.notFoundMessage("User", "username", inputId)
                logger.warn(errorMsg)
                return null
            }

            return UserConverter.entityToDto(userRepository.findById(inputId).get())

        } else {
            return null
        }
    }

}