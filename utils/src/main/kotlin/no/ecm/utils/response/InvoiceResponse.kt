package no.ecm.utils.response

import no.ecm.utils.dto.order.InvoiceDto
import no.ecm.utils.hal.PageDto

class InvoiceResponse (
        code: Int? = null,
        page: PageDto<InvoiceDto>? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<InvoiceDto>(code, page, message, status)