package no.ecm.utils.exception

class NotFoundException(
        message: String,
        val httpCode : Int = 404
) : RuntimeException(message)