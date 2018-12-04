package no.ecm.utils.response

import no.ecm.utils.dto.movie.NowPlayingDto
import no.ecm.utils.hal.PageDto

class NowPlayingReponse(
        code: Int? = null,
        page: PageDto<NowPlayingDto>? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<NowPlayingDto>(code, page, message, status)