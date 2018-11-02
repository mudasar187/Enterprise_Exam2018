package no.ecm.gateway.model.entity

import no.ecm.gateway.controller.RoleType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Auth (

        @Id
        @get:NotBlank
        @get:Size(min = 5, max = 20)
        var userName: String,

        @get:NotBlank
        var password: String,

        @Enumerated(EnumType.STRING)
        var role : RoleType,

        @get:NotNull
        var enabled : Boolean? = null
)