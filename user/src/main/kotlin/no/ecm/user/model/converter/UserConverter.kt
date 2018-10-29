package no.ecm.user.model.converter

import no.ecm.user.model.entity.User
import no.ecm.utils.dto.user.UserDto

object UserConverter {
	
	fun entityToDto(entity: User): UserDto {
		return UserDto(
			username = entity.username,
			age = entity.age,
			name = entity.name,
			email = entity.email
		)
	}
	
	fun dtoToEntity(dto: UserDto) : User {
		return User(
			username = dto.username!!,
			age = dto.age!!,
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
	
}