package no.ecm.utils.response

import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.hal.PageDto

class CinemaResponse (
        code: Int? = null,
        page: PageDto<CinemaDto>? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<CinemaDto>(code, page, message, status)