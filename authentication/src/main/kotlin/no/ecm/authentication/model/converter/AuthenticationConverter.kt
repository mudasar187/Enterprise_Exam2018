package no.ecm.authentication.model.converter

import no.ecm.authentication.model.entity.UserEntity
import no.ecm.utils.dto.auth.AuthenticationDto

object AuthenticationConverter {
    fun entityToDto (entity: UserEntity): AuthenticationDto {
        return AuthenticationDto(
                username = entity.username,
                password = entity.password
        )
    }

    fun dtoToEntity(dto: AuthenticationDto) : UserEntity {
        return UserEntity(dto.username!!, dto.password!!, dto.role!!.toSet(), dto.enabled!!)
    }
}