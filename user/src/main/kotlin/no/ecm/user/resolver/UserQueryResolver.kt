package no.ecm.user.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import no.ecm.user.model.converter.UserConverter
import no.ecm.user.repository.UserRepository
import no.ecm.utils.dto.user.UserDto
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import org.springframework.stereotype.Component

@Component
class UserQueryResolver(
        private var userRepository: UserRepository
): GraphQLQueryResolver {
    
    val logger = logger<UserQueryResolver>()

    fun userById(inputId: String): UserDto? {
        
        if (!userRepository.existsById(inputId)) {
            val errorMsg = ExceptionMessages.notFoundMessage("User", "username", inputId.toString())
            logger.warn(errorMsg)
            return null
        }
        
        return UserConverter.entityToDto(userRepository.findById(inputId).get())
    }

}