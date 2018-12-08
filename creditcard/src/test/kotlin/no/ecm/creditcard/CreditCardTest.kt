package no.ecm.creditcard

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.*
import org.junit.Assert.assertNotNull
import org.junit.Test


class CreditCardTest : TestBase() {
	
	@Test
	fun testCreateAndGetById() {
		
		val username = "johndoe"
		val creditcardNumber = "12345"
		val cvc = 123
		val expDate = "20/01"
		
		val id = createCreditcard(username, creditcardNumber, expDate, cvc)
		
		assertNotNull(id)
		
		getCreditcardById(id!!)!!
			.statusCode(200)
			
			// all queries params are present
			.body("data.creditcardById.size()", equalTo(5))
			
			// check individual params
			.body("data.creditcardById.id", equalTo(id.toString()))
			.body("data.creditcardById.username", equalTo(username))
			.body("data.creditcardById.cardNumber", equalTo(creditcardNumber))
			.body("data.creditcardById.cvc", equalTo(cvc))
			.body("data.creditcardById.expirationDate", equalTo(expDate))
	}
	
	@Test
	fun createDuplicateCreditCard() {
		
		val username = "johndoe"
		val creditcardNumber = "12345"
		val cvc = 123
		val expDate = "20/01"
		
		createCreditcard(username, creditcardNumber, expDate, cvc)
		
		val query = """
                    { "query" :
                         "mutation{createCreditCard(creditCard:{expirationDate:\"$expDate\",cvc: $cvc,username:\"$username\",cardNumber:\"$creditcardNumber\"})}"
                    }
                    """.trimIndent()
		invalidUserQuery(query)!!
			.body("data.createUser", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
	}
	
	@Test
	fun testCreateCreditCardWithInvalidData() {
		
		val username = "johndoe"
		val creditcardNumber = "12345"
		val cvc = 123
		val expDate = "20/01"
		
		val noExpDate = """
                    { "query" :
                         "mutation{createCreditCard(creditCard:{expirationDate:null,cvc: $cvc,username:\"$username\",cardNumber:\"$creditcardNumber\"})}"
                    }
                    """.trimIndent()
		val noCvc = """
                    { "query" :
                         "mutation{createCreditCard(creditCard:{expirationDate:\"$expDate\",cvc: null,username:\"$username\",cardNumber:\"$creditcardNumber\"})}"
                    }
                    """.trimIndent()
		val noUsername = """
                    { "query" :
                         "mutation{createCreditCard(creditCard:{expirationDate:\"$expDate\",cvc: $cvc,username:null,cardNumber:\"$creditcardNumber\"})}"
                    }
                    """.trimIndent()
		val noCreditCardNumber = """
                    { "query" :
                         "mutation{createCreditCard(creditCard:{expirationDate:\"$expDate\",cvc: $cvc,username:\"$username\",cardNumber:null})}"
                    }
                    """.trimIndent()
		
		invalidUserQuery(noExpDate)!!
			.body("data.createUser", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
		
		invalidUserQuery(noCvc)!!
			.body("data.createUser", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
		
		invalidUserQuery(noUsername)!!
			.body("data.createUser", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
		
		invalidUserQuery(noCreditCardNumber)!!
			.body("data.createUser", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
	}
	
	@Test
	fun testGetNonExistingCreditcard() {
		
		getCreditcardById("-1")!!
			.statusCode(200)
			.body("data.creditcardById", equalTo(null))
	}
	
	@Test
	fun testDeleteCreditcard() {
		
		val username = "johndoe"
		val creditcardNumber = "12345"
		val cvc = 123
		val expDate = "20/01"
		
		val id = createCreditcard(username, creditcardNumber, expDate, cvc)
		
		assertNotNull(id)
		
		getCreditcardById(id!!)!!
			.statusCode(200)
		
		//DELETE
		val deleteQuery = """
                    { "query" :
                         "mutation{deleteCreditCardById(id:\"$id\")}"
                    }
                    """.trimIndent()
		
		given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(deleteQuery)
			.post()
			.then()
			.statusCode(200)
			.body("data.deleteCreditCardById", CoreMatchers.notNullValue() )
		
	}
	
	@Test
	fun deleteInvalidCreditCardTest() {
		
		val deleteQueryNumber = """
                    { "query" :
                         "mutation{deleteCreditCardById(id:\"1234567\")}"
                    }
                    """.trimIndent()
		
		given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(deleteQueryNumber)
			.post()
			.then()
			.statusCode(200)
			.body("data.deleteUserById", CoreMatchers.nullValue())
			.body("errors.message", CoreMatchers.notNullValue())
		
	}
	
}