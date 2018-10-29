package no.ecm.creditcard.model.converter

import no.ecm.creditcard.model.entity.CreditCard
import no.ecm.utils.dto.creditCard.CreditCardDto

object CreditCardConverter {
	
	
	fun entityToDto(entity: CreditCard) : CreditCardDto {
		return CreditCardDto(
			id = entity.id.toString(),
			cardNumber = entity.creditcardNumber,
			expirationDate = entity.expirationDate,
			cvc = entity.cvc,
			username = entity.username
		)
	}
	
	fun dtoToEntity(dto: CreditCardDto) : CreditCard {
		return CreditCard(
			id = dto.id!!.toLong(),
			creditcardNumber = dto.cardNumber!!,
			expirationDate = dto.expirationDate!!,
			cvc = dto.cvc!!,
			username = dto.username!!
		)
	}
	
	fun entityListToDtoList(entities: Iterable<CreditCard>): List<CreditCardDto> {
		return entities.map { entityToDto(it) }
	}
	
	fun dtoListToEntityList(dto: Iterable<CreditCardDto>): List<CreditCard> {
		return dto.map { dtoToEntity(it) }
	}
}