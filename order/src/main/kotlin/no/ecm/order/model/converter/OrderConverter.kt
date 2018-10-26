package no.ecm.order.model.converter

import no.ecm.order.model.entity.Order
import no.ecm.utils.dto.order.OrderDto

object OrderConverter {
	
	fun entityToDto(entity: Order): OrderDto {
		
		return OrderDto(
			id = entity.id.toString(),
			username = entity.username,
			orderDate = entity.orderDate,
			couponCode = entity.couponId,
			nowPlayingId = entity.nowPlayingId.toString(),
			tickets = TicketConverter.entityListToDtoList(entity.tickets)
		)
	}
	
	fun dtoToEntity(dto: OrderDto) : Order {
		return Order(
			id = dto.id!!.toLong(),
			username = dto.username!!,
			orderDate = dto.orderDate!!,
			couponId = dto.couponCode!!,
			nowPlayingId = dto.nowPlayingId!!.toLong(),
			tickets = TicketConverter.dtoListToEntityList(dto.tickets!!.toList()).toMutableSet()
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Order>): List<OrderDto> {
		return entities.map { entityToDto(it) }
	}
	
	fun dtoListToEntityList(dto: Iterable<OrderDto>): List<Order> {
		return dto.map { dtoToEntity(it) }
	}
	
}