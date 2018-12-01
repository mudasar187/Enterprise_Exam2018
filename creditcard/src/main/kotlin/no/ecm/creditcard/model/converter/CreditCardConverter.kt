package no.ecm.creditcard.model.converter

import no.ecm.creditcard.model.entity.CreditCard
import no.ecm.utils.dto.creditCard.CreditCardDto
import no.ecm.utils.dto.creditCard.InputCreditCardDto

object CreditCardConverter {
	
	
	fun entityToDto(entity: CreditCard) : CreditCardDto {
		return CreditCardDto(
			id = entity.id?.toString(),
			cardNumber = entity.creditcardNumber,
			expirationDate = entity.expirationDate,
			cvc = entity.cvc,
			username = entity.username
		)
	}
	
	fun dtoToEntity(dto: InputCreditCardDto) : CreditCard {
		return CreditCard(
			creditcardNumber = dto.cardNumber!!,
			expirationDate = dto.expirationDate!!,
			cvc = dto.cvc!!,
			username = dto.username!!
		)
	}
}