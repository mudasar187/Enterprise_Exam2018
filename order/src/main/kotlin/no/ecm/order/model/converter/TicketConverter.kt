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
			seat = entity.seat
		)
	}
	
	fun dtoToEntity(dto: TicketDto) : Ticket {
		return Ticket(
		
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Ticket>): List<TicketDto> {
		return entities.map { entityToDto(it) }
	}
	
	fun dtoListToEntityList(dto: Iterable<TicketDto>): List<Ticket> {
		return dto.map { dtoToEntity(it) }
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