package no.ecm.utils.response

import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.WrappedResponse

class GenreResponse (
        code: Int? = null,
        page: PageDto<GenreDto>? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<GenreDto>(code, page, message, status)