package no.ecm.user.model.converter

import no.ecm.user.model.entity.User
import no.ecm.utils.dto.user.UserDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object UserConverter {
	
	fun entityToDto(entity: User): UserDto {
		return UserDto(
			username = entity.username,
			dateOfBirth = entity.dateOfBitrh.toString(),
			name = entity.name,
			email = entity.email
		)
	}
	
	fun dtoToEntity(dto: UserDto) : User {
		return User(
			username = dto.username!!,
			dateOfBitrh = convertToLocalDate(dto.dateOfBirth!!),
			name = dto.name!!,
			email = dto.email!!
		)
	}
	
	fun entityListToDtoList(entities: Iterable<User>): List<UserDto> {
		return entities.map { entityToDto(it) }
	}
	
	fun dtoListToEntityList(dto: Iterable<UserDto>): List<User> {
		return dto.map { dtoToEntity(it) }
	}

	private fun convertToLocalDate(stringDate: String): LocalDate {
		return LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
	}
	
}