package no.ecm.creditcard.tests

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.creditcard.TestBase
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.Assert
import org.junit.Test


class CreditCardTest : TestBase() {
	
	@Test
	fun test() {
		
		val res = given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.queryParam("query", """
				{
					creditcardById(id: "1"){
    					id, username, cardNumber, cvc, expirationDate
  					}
				}
			""".trimIndent())
			.get()
			.then()
			.statusCode(200)
			
			//all specified fields are showing
			.body("data.creditcardById.size()", equalTo(5))
		
			//result has all filelds by name
			//.body("data.creditcardById", CoreMatchers.hasItem("id"))
			//.body("data.creditcardById", hasItem("username"))
			//.body("data.creditcardById", hasItem("cardNumber"))
			//.body("data.creditcardById", hasItem("cvc"))
			//.body("data.creditcardById", hasItem("expirationDate"))
	}

}