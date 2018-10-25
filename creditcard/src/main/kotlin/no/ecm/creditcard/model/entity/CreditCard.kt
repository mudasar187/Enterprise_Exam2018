package no.ecm.creditcard.model.entity

import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
class CreditCard (


        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:NotNull
        var creditcardNumber: String,

        @get:NotNull
        var expirationDate: ZonedDateTime,

        @get:NotNull
        var cvc: Int,

        @get:NotNull
        var userId: Long
)