package no.ecm.order.model.converter

import no.ecm.order.model.entity.Ticket
import no.ecm.utils.dto.order.TicketDto

object TicketConverter {
	
	fun entityToDto(entity: Ticket) : TicketDto {
		return TicketDto(
			id = entity.id.toString(),
			price = entity.price,
			seatnumber = entity.seatnumber
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
	
}