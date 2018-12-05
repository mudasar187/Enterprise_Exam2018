package no.ecm.utils.exception

class InternalException(
        message: String,
        val httpCode : Int = 500
) : RuntimeException(message)