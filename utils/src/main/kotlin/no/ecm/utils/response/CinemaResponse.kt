package no.ecm.utils.response

import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.WrappedResponse


class CinemaResponse (
        code: Int? = null,
        page: PageDto<CinemaDto>? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<CinemaDto>(code, page, message, status)