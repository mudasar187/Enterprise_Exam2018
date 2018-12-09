package no.ecm.user.service

import graphql.execution.DataFetcherResult
import graphql.servlet.GenericGraphQLError
import no.ecm.user.model.converter.UserConverter
import no.ecm.user.repository.UserRepository
import no.ecm.utils.dto.user.UserDto
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.messages.InfoMessages
import org.springframework.stereotype.Service

@Service
class UserService(
        private var userRepository: UserRepository
)  {

    val logger = logger<UserService>()


    fun createUser(userDto: UserDto): DataFetcherResult<String> {
        when {
            userDto.username.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.missingRequiredField("username")
                logger.warn(errorMsg)
                return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
            }
            userDto.dateOfBirth.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.missingRequiredField("dateOfBirth")
                logger.warn(errorMsg)
                return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
            }
            userDto.email.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.missingRequiredField("email")
                logger.warn(errorMsg)
                return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
            }
            userDto.name.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.missingRequiredField("name")
                logger.warn(errorMsg)
                return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
            }

            userRepository.existsById(userDto.username!!) -> {
                val errorMsg = ExceptionMessages.resourceAlreadyExists("user", "username", userDto.username!!)
                logger.warn(errorMsg)
                return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
            }

            else -> {

                val id = userRepository.save(UserConverter.dtoToEntity(userDto)).username.toString()
                val msg = InfoMessages.entityCreatedSuccessfully("user", id)
                logger.info(msg)

                return DataFetcherResult(id, listOf())
            }
        }
    }
}