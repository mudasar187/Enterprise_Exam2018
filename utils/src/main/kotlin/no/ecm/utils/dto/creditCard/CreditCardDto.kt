package no.ecm.utils.dto.creditCard

open class wCreditCardDto(
        var id: String? = null,
        cardNumber: String? = null,
        expirationDate: String? = null,
        cvc: Int? = null,
        username: String? = null
) : InputCreditCardDto(cardNumber, expirationDate, cvc, username)