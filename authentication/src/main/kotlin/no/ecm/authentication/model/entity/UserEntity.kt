package no.ecm.authentication.model.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name="USERS")
class UserEntity(

        @get:Id
        @get:NotBlank
        var username: String?,

        @get:NotBlank
        var password: String?,

        @get:ElementCollection
        @get:NotNull
        var roles: Set<String>? = setOf(),

        @get:NotNull
        var enabled: Boolean? = true
)