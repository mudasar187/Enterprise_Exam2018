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
	fun testCreateAndGetById() {
		
		
		val username = "johndoe"
		val creditcardNumber = "12345"
		val cvc = 123
		val expDate = "20/01"
		
		val createQuery = """
                    { "query" :
                         "mutation{createCreditCard(creditCard:{expirationDate:\"$expDate\",cvc: $cvc,username:\"$username\",cardNumber:\"$creditcardNumber\"})}"
                    }
                    """.trimIndent()
		
		val id = given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(createQuery)
			.post()
			.then()
			.statusCode(200)
			.extract().body().path<String>("data.createCreditCard")
			//.extract().response().body.prettyPeek()
		
		//println(id)
		
		val getQuery = """
			{
  				creditcardById(id: "$id") {
    				id, username, cardNumber, cvc, expirationDate
  				}
			}
		""".trimIndent()
		
		given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.queryParam("query", getQuery)
			.get()
			.then()
			.statusCode(200)
			
			// all queries params are present
			.body("data.creditcardById.size()", equalTo(5))
			
			// check individual params
			.body("data.creditcardById.id", equalTo(id.toString()))
			.body("data.creditcardById.username", equalTo(username))
			.body("data.creditcardById.cardNumber", equalTo(creditcardNumber))
			.body("data.creditcardById.cvc", equalTo(cvc))
			.body("data.creditcardById.expirationDate", equalTo(expDate))
			
			//print result
			.extract().response().body.prettyPeek()
			
	}
	
}