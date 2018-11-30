package no.ecm.utils.response

import no.ecm.utils.dto.movie.MovieDto
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.WrappedResponse

class MovieResponse(
        code: Int? = null,
        page: PageDto<MovieDto>? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<MovieDto>(code, page, message, status)