package no.ecm.utils.exception

class ConflictException(
        message: String,
        val httpCode : Int = 409
) : RuntimeException(message)