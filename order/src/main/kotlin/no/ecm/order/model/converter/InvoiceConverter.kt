package no.ecm.order.model.converter

import no.ecm.order.model.entity.Invoice
import no.ecm.utils.converter.ConvertionHandler
import no.ecm.utils.dto.order.InvoiceDto
import no.ecm.utils.validation.ValidationHandler

object InvoiceConverter {
	
	fun entityToDto(entity: Invoice): InvoiceDto {
		
		return InvoiceDto(
			id = entity.id.toString(),
			username = entity.username,
			orderDate = entity.orderDate.toString(),
			couponCode = CouponConverter.entityToDto(entity.coupon!!),
			nowPlayingId = entity.nowPlayingId.toString(),
			tickets = TicketConverter.entityListToDtoList(entity.tickets),
			isPaid = entity.paid,
			totalPrice = entity.totalPrice
		)
	}
	
	fun dtoToEntity(dto: InvoiceDto) : Invoice {
		
		val formattedTime = "${dto.orderDate!!}.000000"
		val validatedTimeStamp: String = ValidationHandler.validateTimeFormat(formattedTime)
		val parsedDateTime = ConvertionHandler.convertTimeStampToZonedTimeDate(validatedTimeStamp)
		
		return Invoice(
			id = dto.id!!.toLong(),
			username = dto.username!!,
			orderDate = parsedDateTime!!,
			//coupon = CouponConverter.dtoToEntity(dto.couponCode!!), //FIXME
			nowPlayingId = dto.nowPlayingId!!.toLong(),
			paid = dto.isPaid!!,
			totalPrice = dto.totalPrice
			//tickets = TicketConverter.dtoListToEntityList(dto.tickets!!.toList()).toMutableSet() //FIXME
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Invoice>): MutableList<InvoiceDto> {
		return entities.map { entityToDto(it) }.toMutableList()
	}
	
	fun dtoListToEntityList(dto: Iterable<InvoiceDto>): List<Invoice> {
		return dto.map { dtoToEntity(it) }
	}
	
}