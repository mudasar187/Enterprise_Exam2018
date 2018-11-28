package no.ecm.utils.validation

import no.ecm.utils.exception.ExceptionMessages
import no.ecm.utils.exception.ExceptionMessages.Companion.offsetAndLimitInvalid
import no.ecm.utils.exception.UserInputValidationException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ValidationHandler{
    companion object {

        fun validateId(paramId: String?): Long {
            val id: Long

            try {
                id = paramId!!.toLong()
            } catch (e: Exception) {
                val errorMsg: String = if (paramId.equals("undefined")) {
                    ExceptionMessages.missingRequiredField("$paramId")
                } else {
                    ExceptionMessages.invalidIdParameter()
                }
                throw UserInputValidationException(errorMsg)
            }
            return id
        }
        
        fun validateTimeFormat(paramExpireAt: String): String {
            
            val regex = """^(19|20)\d\d[-](0[1-9]|1[012])[-](0[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]).([0-9]{6})$""".toRegex()
            
            if (regex.matches(paramExpireAt)) {
                return paramExpireAt
            } else {
                throw UserInputValidationException(ExceptionMessages.invalidTimeFormat())
            }
        }

        fun validateLimitAndOffset(offset: Int, limit: Int) {
            if(offset < 0 || limit < 1) {
                throw UserInputValidationException(offsetAndLimitInvalid())
            }
        }
    }
}