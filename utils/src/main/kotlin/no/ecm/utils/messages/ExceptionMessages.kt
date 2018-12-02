package no.ecm.utils.messages

class ExceptionMessages{

    companion object {

        fun notFoundMessage (type: String, paramValue: String, value: String): String {
            return "Can't find ${type.capitalize()} with ${paramValue.capitalize()}: $value"
        }

        fun inputFilterInvalid() : String {
            return "You can only use one filter at time"
        }

        fun offsetAndLimitInvalid(): String {
            return "Offset must be grater than/or equal 0 and limit must be greater than/or equal 1"
        }

        fun tooLargeOffset(offset: Int): String {
            return "Too large offset: $offset"
        }

        fun missingRequiredField(field: String): String {
            return "Missing required field: ${field.capitalize()}"
        }

        fun invalidIdParameter(): String {
            return "Invalid id parameter. This should be a numeric string"
        }
        
        fun invalidTimeFormat(): String {
            return "Bad time format, this follows following formatting rules: \"yyyy-MM-dd HH:mm:ss\""
        }
        
        fun invalidSeatFormat(): String {
            return "Bad seat format, this follows following formatting rules: \"[A-Z][0-9]{1,2}\""
        }
        
        fun invalidJsonFormat(): String {
            return "Invalid JSON-format"
        }

        fun invalidParameter(required: String, received: String): String {
            return "Invalid parameter, expected: ${required.capitalize()}, but received: ${received.capitalize()}"
        }

        fun unableToParse(value: String): String {
            return "Unable to parse object variable: ${value.capitalize()}"
        }

        fun illegalParameter(value: String) : String{
            return "You should not provide parameter: ${value.capitalize()} in this request"
        }

        fun resourceAlreadyExists(type: String, paramValue: String, value: String): String {
            return "${type.capitalize()} with ${paramValue.capitalize()} equal to: '${value.capitalize()}' already exists."
        }

        fun notMachingIds(type: String): String {
            return "The given ${type.capitalize()} in DTO doesn't match the $type in the database"
        }

        fun subIdNotMatchingParentId(subId: String, parentId: String) : String {
            return "Foreign key '${subId.capitalize()}' not match primary key '${parentId.capitalize()}'"
        }

    }

}