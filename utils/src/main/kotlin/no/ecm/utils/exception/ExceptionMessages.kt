package no.ecm.utils.exception

class ExceptionMessages{

    companion object {

        fun notFoundMessage (type: String, value: String): String {
            return "Can not find $type with value $value"
        }

        fun inputFilterInvalid() : String {
            return "You can only use one filter at time"
        }

        fun offsetAndLimitInvalid(): String {
            return "Offset must be grater than 0, limit must be grater than or equal 1"
        }

        fun missingRequiredField(field: String): String {
            return "Missing required field $field"
        }

        fun invalidIdParameter(): String {
            return "Invalid id parameter, This should be a numeric string"
        }

    }

}