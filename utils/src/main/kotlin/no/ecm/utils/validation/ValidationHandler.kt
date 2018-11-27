package no.ecm.utils.validation

import no.ecm.utils.exception.ExceptionMessages
import no.ecm.utils.exception.ExceptionMessages.Companion.offsetAndLimitInvalid
import no.ecm.utils.exception.UserInputValidationException

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

        fun validateLimitAndOffset(offset: Int, limit: Int) {
            if(offset < 0 || limit < 1) {
                throw UserInputValidationException(offsetAndLimitInvalid())
            }
        }
    }
}