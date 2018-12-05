package no.ecm.order.model.converter

import no.ecm.order.model.entity.Invoice
import no.ecm.utils.dto.order.InvoiceDto

object InvoiceConverter {
	
	fun entityToDto(entity: Invoice): InvoiceDto {
		
		return InvoiceDto(
			id = entity.id.toString(),
			username = entity.username,
			orderDate = entity.orderDate,
			couponCode = CouponConverter.entityToDto(entity.coupon!!),
			nowPlayingId = entity.nowPlayingId.toString(),
			tickets = TicketConverter.entityListToDtoList(entity.tickets),
			isPaid = entity.isPaid
		)
	}
	
	fun dtoToEntity(dto: InvoiceDto) : Invoice {
		return Invoice(
			id = dto.id!!.toLong(),
			username = dto.username!!,
			orderDate = dto.orderDate!!,
			//coupon = CouponConverter.dtoToEntity(dto.couponCode!!),
			nowPlayingId = dto.nowPlayingId!!.toLong(),
			isPaid = dto.isPaid!!
			//tickets = TicketConverter.dtoListToEntityList(dto.tickets!!.toList()).toMutableSet()
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Invoice>): MutableList<InvoiceDto> {
		return entities.map { entityToDto(it) }.toMutableList()
	}
	
	fun dtoListToEntityList(dto: Iterable<InvoiceDto>): List<Invoice> {
		return dto.map { dtoToEntity(it) }
	}
	
}