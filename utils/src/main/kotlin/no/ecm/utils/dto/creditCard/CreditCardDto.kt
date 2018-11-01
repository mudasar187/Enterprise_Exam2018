package no.ecm.utils.dto.creditCard

data class CreditCardDto(
    
    var id: String? = null,
    
    var cardNumber: String? = null,
    
    var expirationDate: String? = null,
    
    var cvc: Int? = null,
    
    var username: String? = null
)