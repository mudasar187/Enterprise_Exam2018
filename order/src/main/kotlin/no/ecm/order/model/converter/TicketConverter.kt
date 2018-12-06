package no.ecm.order.model.converter

import no.ecm.order.model.entity.Ticket
import no.ecm.utils.dto.order.TicketDto
import no.ecm.utils.hal.PageDto
import kotlin.streams.toList

object TicketConverter {
	
	fun entityToDto(entity: Ticket) : TicketDto {
		return TicketDto(
			id = entity.id.toString(),
			price = entity.price,
			seat = entity.seat,
			invoiceId = entity.invoiceId.toString()
		)
	}
	
	fun dtoToEntity(dto: TicketDto) : Ticket {
		return Ticket(
			price = dto.price,
			invoiceId = dto.invoiceId!!.toLong(),
			seat = dto.seat
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Ticket>): MutableList<TicketDto> {
		return entities.map { entityToDto(it) }.toMutableList()
	}
	
	fun dtoListToEntityList(dto: Iterable<TicketDto>): MutableList<Ticket> {
		return dto.map { dtoToEntity(it) }.toMutableList()
	}
	
	fun dtoListToPageDto(ticketDtoList: List<TicketDto>,
						 offset: Int,
						 limit: Int): PageDto<TicketDto> {
		
		val dtoList: MutableList<TicketDto> =
			ticketDtoList.stream()
				.skip(offset.toLong())
				.limit(limit.toLong())
				.toList().toMutableList()
		
		return PageDto(
			list = dtoList,
			rangeMin = offset,
			rangeMax = offset + dtoList.size - 1,
			totalSize = ticketDtoList.size
		)
	}
	
}