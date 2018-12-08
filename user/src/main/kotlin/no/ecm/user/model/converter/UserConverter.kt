package no.ecm.user.model.converter

import no.ecm.user.model.entity.UserEntity
import no.ecm.utils.dto.user.UserDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object UserConverter {
	
	fun entityToDto(entity: UserEntity): UserDto {
		return UserDto(
			username = entity.username,
			dateOfBirth = entity.dateOfBirth.toString(),
			name = entity.name,
			email = entity.email
		)
	}
	
	fun dtoToEntity(dto: UserDto) : UserEntity {
		return UserEntity(
			username = dto.username!!,
			dateOfBirth = convertToLocalDate(dto.dateOfBirth!!),
			name = dto.name!!,
			email = dto.email!!
		)
	}

	private fun convertToLocalDate(stringDate: String): LocalDate {
		return LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
	}
	
}