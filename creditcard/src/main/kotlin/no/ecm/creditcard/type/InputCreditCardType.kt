package no.ecm.creditcard.type

open class InputCreditCardType(

        var cardNumber: String? = null,
        var expirationDate: String? = null,
        var cvc: Int? = null,
        var username: String? = null
)