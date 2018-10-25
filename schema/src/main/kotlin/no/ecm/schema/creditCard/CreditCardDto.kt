package no.ecm.schema.creditCard

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate

@ApiModel("DTO representing a Credit Card")
data class CreditCardDto(

        @ApiModelProperty("The id of a credit card")
        var id: String? = null,

        @ApiModelProperty("The card number of a credit card")
        var cardNumber: String? = null,

        @ApiModelProperty("The expiration date of a credit card")
        var expirationDate: LocalDate? = null,

        @ApiModelProperty("The CVC code of a credit card")
        var cvc: Int? = null
)