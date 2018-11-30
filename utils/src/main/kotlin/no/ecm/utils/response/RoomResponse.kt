package no.ecm.utils.response

import no.ecm.utils.dto.cinema.RoomDto
import no.ecm.utils.hal.PageDto

class RoomResponse (
        code: Int? = null,
        page: PageDto<RoomDto>? = null,
        message: String? = null,
        status: ResponseStatus? = null
): WrappedResponse<RoomDto>(code, page, message, status)