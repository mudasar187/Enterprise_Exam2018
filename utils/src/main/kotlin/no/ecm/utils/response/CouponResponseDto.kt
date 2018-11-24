package no.ecm.utils.response

import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.hal.PageDto

class CouponResponseDto (
	code: Int? = null,
	page: PageDto<CouponDto>? = null,
	message: String? = null,
	status: ResponseStatus? = null
) : WrappedResponse<CouponDto>(code, page, message, status)