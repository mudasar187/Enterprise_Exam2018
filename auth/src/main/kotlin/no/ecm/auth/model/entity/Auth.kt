package no.ecm.auth.model.entity

import no.ecm.auth.controller.RoleType
import javax.persistence.*
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