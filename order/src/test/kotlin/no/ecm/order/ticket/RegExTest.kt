package no.ecm.order.ticket

import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

class RegExTest {
	
	@Test
	fun regexTest() {
		
		val regex = "^[A-Z][0-9]{1,2}".toRegex()
		
		assertTrue(regex.matches("A5"))
		assertTrue(regex.matches("A10"))
		assertFalse(regex.matches("AA"))
		assertFalse(regex.matches("*5"))
		assertFalse(regex.matches("abc123"))
		assertFalse(regex.matches("A!"))
		assertFalse(regex.matches("A123"))
		assertFalse(regex.matches("AA12"))
		
	}
}