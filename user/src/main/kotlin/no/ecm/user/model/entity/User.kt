package no.ecm.user.model.entity

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.*

@Entity
class User (

    @get:Id
    @Size(min = 5, max = 20)
    @NotBlank
    var username: String? = null,

    @get:NotNull
    var dateOfBitrh: LocalDate,

    @get:NotBlank
    @get:Size(max = 128)
    var name: String,

    @get:NotBlank
    @get:Size(max = 128)
    var email: String

)