package no.ecm.order.model.converter

import no.ecm.order.model.entity.Invoice
import no.ecm.utils.converter.ConvertionHandler
import no.ecm.utils.dto.order.InvoiceDto
import no.ecm.utils.validation.ValidationHandler

object InvoiceConverter {
	
	fun entityToDto(entity: Invoice): InvoiceDto {
		
		val invoiceDto = InvoiceDto(
			id = entity.id.toString(),
			username = entity.username,
			orderDate = entity.orderDate.toString(),
			nowPlayingId = entity.nowPlayingId.toString(),
			tickets = TicketConverter.entityListToDtoList(entity.tickets),
			isPaid = entity.paid,
			totalPrice = entity.totalPrice
		)

		if(entity.coupon != null){
			invoiceDto.couponCode = CouponConverter.entityToDto(entity.coupon!!)
		}
		return invoiceDto
	}
	
	fun dtoToEntity(dto: InvoiceDto) : Invoice {
		
		val formattedTime = "${dto.orderDate!!}.000000"
		val validatedTimeStamp: String = ValidationHandler.validateTimeFormat(formattedTime)
		val parsedDateTime = ConvertionHandler.convertTimeStampToZonedTimeDate(validatedTimeStamp)
		
		return Invoice(
			username = dto.username!!,
			orderDate = parsedDateTime!!,
			nowPlayingId = dto.nowPlayingId!!.toLong(),
			paid = dto.isPaid!!,
			totalPrice = dto.totalPrice
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Invoice>): MutableList<InvoiceDto> {
		return entities.map { entityToDto(it) }.toMutableList()
	}
	
}