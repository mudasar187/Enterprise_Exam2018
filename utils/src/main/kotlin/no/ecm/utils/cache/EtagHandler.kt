package no.ecm.utils.cache

import no.ecm.utils.exception.PreconditionException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.messages.ExceptionMessages.Companion.missingRequiredHeader
import no.ecm.utils.messages.ExceptionMessages.Companion.preConditionFailed

class EtagHandler<T> {

    fun generateEtag(dto: T? = null, list: MutableList<T>? = null): String {
        return when {
            dto != null -> dto.hashCode().toString()
            list != null -> list.hashCode().toString()
            else -> throw NullPointerException("Unable to generate eTag, object was empty")
        }
    }

    fun validateEtags(expected: T, request: String?) {
        if (request.isNullOrBlank()){
            throw UserInputValidationException(missingRequiredHeader("If-Match"))
        }

        if (generateEtag(expected) != request){
            throw PreconditionException(preConditionFailed())
        }
    }
}