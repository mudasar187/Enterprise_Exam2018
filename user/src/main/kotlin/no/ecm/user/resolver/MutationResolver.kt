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
            } else {
                "${e.javaClass}: ${e.message}"
            }
            return DataFetcherResult<String>(null, listOf(GenericGraphQLError(msg)))
        }

        return DataFetcherResult(id.toString(), listOf())

    }

    fun deleteUserById(inputId: String): Boolean {


        if (!userRepository.existsById(inputId)) {
            return false
        }

        userRepository.deleteById(inputId)

        return true
    }

    fun updateUserById(userId: String, name: String?, email: String?): DataFetcherResult<Boolean> {


        if (!userRepository.existsById(userId)) {
            return DataFetcherResult<Boolean>(null, listOf(
                    GenericGraphQLError("No user with id $userId exists")))
        }


        try {
            val user = userRepository.findById(userId).get()

            if (!name.isNullOrEmpty()){
                user.name = name!!
            }

            if (!email.isNullOrEmpty()){
                user.email = email!!
            }

            userRepository.save(user)

        } catch (e: Exception) {
            val cause = Throwables.getRootCause(e)
            if (cause is ConstraintViolationException) {
                return DataFetcherResult<Boolean>(null, listOf(
                        GenericGraphQLError("Violated constraints: ${cause.message}")))
            }
            throw e
        }

        return DataFetcherResult(true, listOf())
    }

}