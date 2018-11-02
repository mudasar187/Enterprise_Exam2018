package no.ecm.gateway.model.converter

import no.ecm.gateway.controller.RoleType
import no.ecm.gateway.model.entity.Auth
import no.ecm.utils.dto.auth.AuthDto

object AuthConverter {
    fun entityToDto (entity: Auth): AuthDto {
        return AuthDto(
                userName = entity.userName,
                password = entity.password
        )
    }

    fun dtoToEntity(dto: AuthDto) : Auth {
        return Auth(dto.userName!!, dto.password!!, (RoleType.valueOf(dto.role!!)), dto.enabled!!)
    }
}