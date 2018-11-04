package no.ecm.movie.model.response

import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.response.WrappedResponse

class GenreResponse (
        code: Int? = null,
        data: List<GenreDto>? = null,
        message: String? = null,
        status: ResponseStatus? = null

) : WrappedResponse<List<GenreDto>>(code, data, message, status)