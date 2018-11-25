package no.ecm.order.service

import com.google.common.base.Throwables
import no.ecm.order.model.converter.TicketConverter
import no.ecm.order.repository.ticket.TicketRepository
import no.ecm.utils.dto.order.TicketDto
import no.ecm.utils.hal.HalLink
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.TicketResponseDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import javax.validation.ConstraintViolationException

@Service
class TicketService {
	fun get(paramId: String?, offset: Int, limit: Int): ResponseEntity<WrappedResponse<TicketDto>> {
		
		if(offset < 0 || limit < 1) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				TicketResponseDto(
					code = HttpStatus.BAD_REQUEST.value(),
					message = "Invalid offset or limit.	 Rules: Offset > 0 && limit >= 1"
				).validated()
			)
		}
		
		val ticketResultList: List<TicketDto>
		val builder = UriComponentsBuilder.fromPath("/tickets")
		
		if (paramId.isNullOrBlank()) {
			
			ticketResultList = TicketConverter.entityListToDtoList(repository.findAll())
		} else {
			
			val id = try { paramId!!.toLong() }
			
			catch (e: Exception) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					TicketResponseDto(
						code = HttpStatus.NOT_FOUND.value(),
						message = "Invalid id: $paramId"
					).validated()
				)
			}
			
			val entity = repository.findById(id).orElse(null)
				?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					TicketResponseDto(
						code = HttpStatus.NOT_FOUND.value(),
						message = "could not find ticket with ID: $id"
					).validated()
				)
			
			ticketResultList = listOf(TicketConverter.entityToDto(entity))
			
			builder.queryParam("id", paramId)
			
		}
		
		if (offset != 0 && offset >= ticketResultList.size) {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				TicketResponseDto(
					code = HttpStatus.BAD_REQUEST.value(),
					message = "Too large offset, size of result is ${ticketResultList.size}"
				).validated()
			)
		}
		
		builder.queryParam("limit", limit)
		
		val dto = TicketConverter.dtoListToPageDto(ticketResultList, offset, limit)
		
		// Build HalLinks
		dto._self = HalLink(builder.cloneBuilder()
			.queryParam("offset", offset)
			.build().toString()
		)
		
		if (!ticketResultList.isEmpty() && offset > 0) {
			dto.previous = HalLink(builder.cloneBuilder()
				.queryParam("offset", Math.max(offset - limit, 0))
				.build().toString()
			)
		}
		
		if (offset + limit < ticketResultList.size) {
			dto.next = HalLink(builder.cloneBuilder()
				.queryParam("offset", (offset + limit))
				.build().toString()
			)
		}
		
		val etag = ticketResultList.hashCode().toString()
		
		return ResponseEntity.status(HttpStatus.OK)
			.eTag(etag)
			.body(
				TicketResponseDto(
					code = HttpStatus.OK.value(),
					page = dto
				).validated()
			)
	}
	
	fun create(dto: TicketDto): ResponseEntity<WrappedResponse<TicketDto>> {
		
		if (dto.id != null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				TicketResponseDto(
					code = HttpStatus.NOT_FOUND.value(),
					message = "id != null, you cannot create a coupon with predefined id"
				).validated()
			)
		}
		
		if (dto.price!!.isNaN() || dto.seat.isNullOrEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				TicketResponseDto(
					code = HttpStatus.BAD_REQUEST.value(),
					message = "You need to specify a code, description and expireAt when creating a Coupon, " +
						"please check documentation for more info"
				).validated()
			)
		}
		
		val id = try { repository.createTicket(dto.price!!, dto.seat) }
		
		catch (e: Exception) {
			
			if (Throwables.getRootCause(e) is ConstraintViolationException) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					TicketResponseDto(
						code = HttpStatus.BAD_REQUEST.value(),
						message = "Error while creating a ticket, contact sys-adm"
					).validated()
				)
			}
			throw e
			
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).body(
			TicketResponseDto(
				code = HttpStatus.CREATED.value(),
				page = PageDto(list = mutableListOf(TicketDto(id = id.toString()))),
				message = "Coupon with id: $id was created"
			).validated()
		)
	}
	
	fun delete(paramId: String): ResponseEntity<WrappedResponse<TicketDto>> {
		
		val id = try { paramId.toLong() }
		
		catch (e: Exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				TicketResponseDto(
					code = HttpStatus.BAD_REQUEST.value(),
					message = "Invalid id: $paramId"
				).validated()
			)
		}
		
		//if the given is is not registred in the DB
		if (!repository.existsById(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				TicketResponseDto(
					code = HttpStatus.NOT_FOUND.value(),
					message = "Could not find coupon with id: $id"
				).validated()
			)
		}
		
		repository.deleteById(id)
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
			TicketResponseDto(
				code = HttpStatus.NO_CONTENT.value(),
				message = "Coupon with id: $id successfully deleted"
			).validated()
		)
	}
	
	@Autowired
	private lateinit var repository: TicketRepository

}