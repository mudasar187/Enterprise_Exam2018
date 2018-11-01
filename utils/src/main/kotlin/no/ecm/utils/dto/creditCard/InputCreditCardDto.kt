package no.ecm.utils.dto.creditCard

open class InputCreditCardDto(
        var cardNumber: String? = null,
        var expirationDate: String? = null,
        var cvc: Int? = null,
        var username: String? = null
)