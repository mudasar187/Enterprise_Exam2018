package no.ecm.utils.converter

import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.exception.UserInputValidationException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ConvertionHandler {
	
	companion object {
		
		// Converting string to ZonedDateTime
		// inspired by this answer from StackOverflow
		// https://stackoverflow.com/a/44487882/10396560
		fun convertTimeStampToZonedTimeDate(timestamp: String): ZonedDateTime? {
			
			val pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS"
			val parser: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault())
			
			return try {
				
				ZonedDateTime.parse(timestamp, parser)
				
			} catch (e: Exception) {
				
				throw UserInputValidationException(ExceptionMessages.invalidTimeFormat())
				
			}
		}
		
	}
	
}