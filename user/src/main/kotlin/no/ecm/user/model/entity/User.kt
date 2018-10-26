package no.ecm.user.model.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.*

@Entity
class User (

    @get:Id
    @Size(min = 5, max = 20)
    @NotBlank
    var username: String? = null,

    @get:NotNull
    @get:Min(15)
    @get:Max(110)
    var age: Int,

    @get:NotBlank
    @get:Size(max = 128)
    var name: String,

    @get:NotBlank
    @get:Size(max = 128)
    var email: String

)