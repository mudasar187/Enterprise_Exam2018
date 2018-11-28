package no.ecm.utils.hal

import no.ecm.utils.exception.ExceptionMessages
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import no.ecm.utils.validation.ValidationHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.util.UriComponentsBuilder

class HalLinkGenerator<T> {



    fun generateHalLinks(totalList: MutableList<T>, pageDto: PageDto<T>, builder: UriComponentsBuilder, limit: Int, offset: Int): ResponseEntity<WrappedResponse<T>>{

        ValidationHandler.validateLimitAndOffset(offset, limit)

        builder.queryParam("limit", limit)

        if (offset != 0 && offset >= totalList.size) {
            throw UserInputValidationException(ExceptionMessages.toLargeOffset(offset))
        }

        // Build HalLinks
        pageDto._self = HalLink(builder.cloneBuilder()
                .queryParam("offset", offset)
                .build().toString()
        )

        if (!totalList.isEmpty() && offset > 0) {
            pageDto.previous = HalLink(builder.cloneBuilder()
                    .queryParam("offset", Math.max(offset - limit, 0))
                    .build().toString()
            )
        }

        if (offset + limit < totalList.size) {
            pageDto.next = HalLink(builder.cloneBuilder()
                    .queryParam("offset", (offset + limit))
                    .build().toString()
            )
        }

        val etag = totalList.hashCode().toString()

        return ResponseEntity.status(HttpStatus.OK)
                .eTag(etag)
                .body(ResponseDto(
                        code = HttpStatus.OK.value(),
                        page = pageDto
                ).validated()
                )
    }

}