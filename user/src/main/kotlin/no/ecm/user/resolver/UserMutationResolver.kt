package no.ecm.user.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import graphql.execution.DataFetcherResult
import graphql.servlet.GenericGraphQLError
import no.ecm.user.model.converter.UserConverter
import no.ecm.user.repository.UserRepository
import no.ecm.utils.dto.user.UserDto
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.messages.InfoMessages
import no.ecm.utils.messages.InfoMessages.Companion.entitySuccessfullyDeleted
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class UserMutationResolver(
	private var userRepository: UserRepository
) : GraphQLMutationResolver {
	
	val logger = logger<UserMutationResolver>()
	
	fun createUser(input: UserDto): DataFetcherResult<String> {
		val auth = SecurityContextHolder.getContext().authentication

		if (auth.name == input.username || isAdmin()) {
			when {
				input.username.isNullOrBlank() -> {
					val errorMsg = ExceptionMessages.missingRequiredField("username")
					logger.warn(errorMsg)
					return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
				}
				input.dateOfBirth.isNullOrBlank() -> {
					val errorMsg = ExceptionMessages.missingRequiredField("dateOfBirth")
					logger.warn(errorMsg)
					return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
				}
				input.email.isNullOrBlank() -> {
					val errorMsg = ExceptionMessages.missingRequiredField("email")
					logger.warn(errorMsg)
					return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
				}
				input.name.isNullOrBlank() -> {
					val errorMsg = ExceptionMessages.missingRequiredField("name")
					logger.warn(errorMsg)
					return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
				}

				userRepository.existsById(input.username!!) -> {
					val errorMsg = ExceptionMessages.resourceAlreadyExists("user", "username", input.username!!)
					logger.warn(errorMsg)
					return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
				}

				else -> {

					val id = userRepository.save(UserConverter.dtoToEntity(input)).username.toString()
					val msg = InfoMessages.entityCreatedSuccessfully("user", id)
					logger.info(msg)

					return DataFetcherResult(id, listOf())
				}
			}
		} else {
			val errorMsg = ExceptionMessages.unauthorizedUser(auth.name)
			logger.warn(errorMsg)
			return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
		}
	}
	
	fun deleteUserById(inputId: String): DataFetcherResult<String> {
		val auth = SecurityContextHolder.getContext().authentication

		if (auth.name == inputId || isAdmin()) {

			if (!userRepository.existsById(inputId)) {
				val errorMsg = ExceptionMessages.notFoundMessage("User", "username", inputId)
				logger.warn(errorMsg)
				return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
			}

			userRepository.deleteById(inputId)

			val msg = entitySuccessfullyDeleted("User", inputId)
			logger.info(msg)
			return DataFetcherResult(msg, listOf())
		} else {
			val errorMsg = ExceptionMessages.unauthorizedUser(auth.name)
			logger.warn(errorMsg)
			return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
		}
	}
	
	fun updateUserById(userId: String?, name: String?, email: String?): DataFetcherResult<String> {
		val auth = SecurityContextHolder.getContext().authentication

		if (auth.name == userId || isAdmin()) {
			when {
				userId.isNullOrBlank() -> {
					val errorMsg = ExceptionMessages.missingRequiredField("userId")
					logger.warn(errorMsg)
					return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
				}

				name.isNullOrBlank() -> {
					val errorMsg = ExceptionMessages.missingRequiredField("name")
					logger.warn(errorMsg)
					return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
				}

				email.isNullOrBlank() -> {
					val errorMsg = ExceptionMessages.missingRequiredField("email")
					logger.warn(errorMsg)
					return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
				}

				!userRepository.existsById(userId!!) -> {
					val errorMsg = ExceptionMessages.notFoundMessage("User", "username", userId!!)
					logger.warn(errorMsg)
					return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
				}

				else -> {

					val user = userRepository.findById(userId).get()

					if (user.username!! != userId) {
						val errorMsg = ExceptionMessages.subIdNotMatchingParentId(user.username!!, userId)
						logger.warn(errorMsg)
						return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
					}

					user.name = name!!
					user.email = email!!

					userRepository.save(user)
					val msg = InfoMessages.entitySuccessfullyUpdated("user", userId)
					logger.info(msg)
					return DataFetcherResult(msg, listOf())
				}
			}
		} else {
			val errorMsg = ExceptionMessages.unauthorizedUser(auth.name)
			logger.warn(errorMsg)
			return DataFetcherResult<String>(null, listOf(GenericGraphQLError(errorMsg)))
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