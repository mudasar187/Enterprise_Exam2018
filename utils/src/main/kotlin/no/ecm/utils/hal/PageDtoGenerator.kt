package no.ecm.utils.hal

import no.ecm.utils.validation.ValidationHandler
import kotlin.streams.toList

class PageDtoGenerator<T> {

    fun generatePageDto(list: List<T>, offset: Int, limit: Int) : PageDto<T> {
        ValidationHandler.validateLimitAndOffset(offset, limit)

        val dtoList: MutableList<T> =
                list.stream()
                        .skip(offset.toLong())
                        .limit(limit.toLong())
                        .toList().toMutableList()

        return PageDto(
                list = dtoList,
                rangeMin = offset,
                rangeMax = offset + dtoList.size - 1,
                totalSize = list.size
        )
    }
}