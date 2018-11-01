package no.ecm.user.scalar

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Component
class MyBirthdayScalar : GraphQLScalarType("MyBirthdayScalar", "Localdate scalar", MyDateTimeScalarCoercing())

private class MyDateTimeScalarCoercing : Coercing<LocalDate, String> {


    override fun serialize(input: Any): String {
        if (input is LocalDate) {
            return input.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }

        val result = convertString(input)
                ?: throw CoercingSerializeException("Invalid value '$input' for Localdate")

        return result.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    override fun parseValue(input: Any): LocalDate {

        return convertString(input)
                ?: throw CoercingParseValueException("Invalid value '$input' for ZonedDateTime")
    }

    override fun parseLiteral(input: Any): LocalDate? {

        if (input !is StringValue){
            return null
        }

        return convertString(input.value)
    }

    private fun convertString(input: Any): LocalDate? {

        if (input is String) {
            return try {
                LocalDate.parse(input)
            } catch (e: DateTimeParseException) {
                null
            }
        }

        return null
    }
}