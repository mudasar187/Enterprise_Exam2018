package no.ecm.utils.response

import no.ecm.utils.dto.order.TicketDto
import no.ecm.utils.hal.PageDto

class TicketResponseDto (
	code: Int? = null,
	page: PageDto<TicketDto>? = null,
	message: String? = null,
	status: ResponseStatus? = null
) : WrappedResponse<TicketDto>(code, page, message, status)