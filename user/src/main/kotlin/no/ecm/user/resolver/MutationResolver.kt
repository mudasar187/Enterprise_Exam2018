package no.ecm.user.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.google.common.base.Throwables
import graphql.execution.DataFetcherResult
import graphql.servlet.GenericGraphQLError
import no.ecm.user.model.converter.UserConverter
import no.ecm.user.repository.UserRepository
import no.ecm.utils.dto.user.UserDto
import org.springframework.stereotype.Component
import javax.validation.ConstraintViolationException

@Component
class MutationResolver(
        private var userRepository: UserRepository
): GraphQLMutationResolver {


    fun createUser(input: UserDto): DataFetcherResult<String> {

        val id = try {
            userRepository.save(UserConverter.dtoToEntity(input)).username
        } catch (e: Exception) {
            val cause = Throwables.getRootCause(e)
            val msg = if (cause is ConstraintViolationException) {
                "Violated constraints: ${cause.message}"
            }else {
                e.message
            }
            return DataFetcherResult<String>(null, listOf(GenericGraphQLError(msg)))
        }

        return DataFetcherResult(id.toString(), listOf())

    }

    fun deleteUserById(inputId: String): String? {


        if (!userRepository.existsById(inputId)) {
            return null
        }

        userRepository.deleteById(inputId)

        return inputId
    }

}