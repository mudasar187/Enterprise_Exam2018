package no.ecm.authentication.model.converter

import no.ecm.authentication.model.entity.Authentication
import no.ecm.utils.dto.auth.AuthenticationDto

object AuthenticationConverter {
    fun entityToDto (entity: Authentication): AuthenticationDto {
        return AuthenticationDto(
                userName = entity.username,
                password = entity.password
        )
    }

    fun dtoToEntity(dto: AuthenticationDto) : Authentication {
        return Authentication(dto.userName!!, dto.password!!, dto.role!!, dto.enabled!!)
    }
}