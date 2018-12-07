package no.ecm.authentication.model.converter

import no.ecm.authentication.model.entity.Authentication
import no.ecm.utils.dto.auth.AuthDto

object AuthConverter {
    fun entityToDto (entity: Authentication): AuthDto {
        return AuthDto(
                userName = entity.username,
                password = entity.password
        )
    }

    fun dtoToEntity(dto: AuthDto) : Authentication {
        return Authentication(dto.userName!!, dto.password!!, dto.role!!, dto.enabled!!)
    }
}