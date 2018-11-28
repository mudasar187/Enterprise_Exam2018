package no.ecm.utils.exception

class ExceptionMessages{

    companion object {

        fun notFoundMessage (type: String, paramValue: String, value: String): String {
            return "Can't find $type with $paramValue: $value"
        }

        fun inputFilterInvalid() : String {
            return "You can only use one filter at time"
        }

        fun offsetAndLimitInvalid(): String {
            return "Offset must be grater than 0 and limit must be greater than/or equal 1"
        }

        fun toLargeOffset(offset: Int): String {
            return "Too large offset: $offset"
        }

        fun missingRequiredField(field: String): String {
            return "Missing required field: $field"
        }

        fun invalidIdParameter(): String {
            return "Invalid id parameter. This should be a numeric string"
        }
        
        fun tooLargeOffset(size: Int): String {
            return "Too large offset, size of result is $size"
        }
        
        fun invalidTimeFormat(): String {
            return "Bad expireAt format!, this follows following formatting rules: \"yyyy-MM-dd HH:mm:ss\""
        }
        
        fun idInCreationDtoBody(type: String): String {
            return "You cannot create a $type with predefined id"
        }
        
        fun createEntity(type: String): String {
            return "Error while creating a $type"
        }
        
        fun deleteEntity(type: String): String {
            return "Error while deleting $type"
        }

        fun invalidParameter(required: String, received: String): String {
            return "Invalid parameter, expected: $required, but received: $received"
        }

        fun unableToParse(value: String): String {
            return "Unable to parse object variable: $value"
        }

        fun illegalParameter(value: String) : String{
            return "You should not provide parameter: $value in this request!"
        }

        fun resourceAlreadyExists(type: String, paramValue: String, value: String): String {
            return "$type with $paramValue equal to: '$value' already exists."
        }

    }

}