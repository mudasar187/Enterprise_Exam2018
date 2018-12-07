package no.ecm.authentication.model.entity

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "USERS")
class Authentication (

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