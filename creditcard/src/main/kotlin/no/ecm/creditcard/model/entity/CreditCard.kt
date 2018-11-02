package no.ecm.creditcard.model.entity

import org.springframework.format.annotation.NumberFormat
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class CreditCard (

        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:NotNull
        var creditcardNumber: String,

        @get:NotNull
        var expirationDate: String,

        @get:NotNull
        var cvc: Int,

        @get:NotBlank
        var username: String
)