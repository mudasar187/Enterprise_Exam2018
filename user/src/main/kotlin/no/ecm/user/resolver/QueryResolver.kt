package no.ecm.user.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import no.ecm.user.model.converter.UserConverter
import no.ecm.user.repository.UserRepository
import no.ecm.utils.dto.user.UserDto
import org.springframework.stereotype.Component

@Component
class QueryResolver(
        private var userRepository: UserRepository
): GraphQLQueryResolver {


    fun userById(inputId: String): UserDto? {

        val user = userRepository.findById(inputId).orElse(null) ?: return null

        return UserConverter.entityToDto(user)
    }

}