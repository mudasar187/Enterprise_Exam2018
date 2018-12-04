package no.ecm.utils.exception

class PreconditionException(
        message: String,
        val httpCode : Int = 412
) : RuntimeException(message)