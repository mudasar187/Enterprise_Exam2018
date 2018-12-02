package no.ecm.utils.cache

import no.ecm.utils.exception.UserInputValidationException

class EtagGenerator<T> {

    fun generateEtag(dto: T? = null, list: MutableList<T>? = null): String {
        return when {
            dto != null -> dto.hashCode().toString()
            list != null -> list.hashCode().toString()
            else -> throw NullPointerException("Unable to generate eTag")
        }
    }

    //TODO not yet implemented correctly
    fun validateEtags(expected: T, request: String) {
        if (generateEtag(expected) != request){
            throw UserInputValidationException("")
        }
    }
}