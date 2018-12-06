package no.ecm.gateway.model.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "USERS")
class Auth (

        @Id
        @get:NotBlank
        @get:Size(min = 5, max = 20)
        var username: String,

        @get:NotBlank
        var password: String,

        @get:ElementCollection(targetClass = String::class)
        @get:NotNull
        var roles: Set<String>? = setOf(),

        @get:NotNull
        var enabled : Boolean? = true
)