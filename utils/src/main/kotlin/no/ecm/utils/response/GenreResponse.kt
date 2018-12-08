package no.ecm.utils.response

import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.hal.PageDto

class GenreResponse (
        code: Int? = null,
        page: PageDto<GenreDto>? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<GenreDto>(code, page, message, status)