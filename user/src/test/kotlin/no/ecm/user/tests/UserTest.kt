package no.ecm.user.tests

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.user.TestBase
import org.hamcrest.Matchers.equalTo
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test


class UserTest : TestBase() {
	
	
	@Test
	fun testCreateAndGetById() {
		
		val username = "frodo55"
		val email = "frodo@shire.no"
		val name = "Frodo Baggins"
		val dateOfBirth = "2018-11-04"
		
		val usernameRes = createUser(username, dateOfBirth, name, email)
		
		assertNotNull(usernameRes)
		assertEquals(username, usernameRes)
		
		getUserByUsername(username)!!
			.statusCode(200)
			
			// all queries params are present
			.body("data.userById.size()", equalTo(4))
			
			// check individual params
			.body("data.userById.username", equalTo(username))
			.body("data.userById.email", equalTo(email))
			.body("data.userById.name", equalTo(name))
			.body("data.userById.dateOfBirth", equalTo(dateOfBirth))
	}
	
	
	@Test
	fun testCreateUserWithMissingArguments() {
		
		val dateOfBirth = "2018-03-03"
		
		val createQuery = """
                    { "query" :
                         "mutation{createUser(user:{dateOfBirth:\"$dateOfBirth\"})}"
                    }
                    """.trimIndent()
		
		given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(createQuery)
			.post()
			.then()
			.statusCode(200)
			.body("data.createUser", equalTo(null))
			//.extract().body().path<String>("data.createUser")
		
		//TODO get the errormessages in errors.message[0]. Problem in converter?
			.extract().response().body.prettyPeek()
	}
	
	@Test
	fun testGetNonExistingCreditcard() {
		
		getUserByUsername("arhurdent42")!!
			.statusCode(200)
			.body("data.userById", equalTo(null))
	}
	
	@Test
	fun testDeleteCreditcard() {
		
		val username = "frodo55"
		val email = "frodo@shire.no"
		val name = "Frodo Baggins"
		val dateOfBirth = "2018-11-04"
		
		val usernameRes = createUser(username, dateOfBirth, name, email)
		
		assertNotNull(usernameRes)
		assertEquals(username, usernameRes)
		
		getUserByUsername(username)!!
			.statusCode(200)
		
		//DELETE
		val deleteQuery = """
                    { "query" :
                         "mutation{deleteUserById(id:\"$username\")}"
                    }
                    """.trimIndent()
		
		given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(deleteQuery)
			.post()
			.then()
			.statusCode(200)
			.body("data.deleteUserById", equalTo(true.toString()))
	}
	
	@Test
	fun testUpdateUser() {
		val username = "frodo55"
		val email = "frodo@shire.no"
		val name = "Frodo Baggins"
		val dateOfBirth = "2018-11-04"
		
		val usernameRes = createUser(username, dateOfBirth, name, email)
		
		assertNotNull(usernameRes)
		assertEquals(username, usernameRes)
		
		getUserByUsername(username)!!
			.statusCode(200)
		
		//UPDATE
		val updatedEmail = "bilbo@shire.no"
		val updatedName = "Bilbo Baggins"
		
		
		val updateQuery = """
                    { "query" :
                         "mutation{updateUserById(id:\"$username\", name:\"$updatedName\", email:\"$updatedEmail\")}"
                    }
                    """.trimIndent()
		
		given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(updateQuery)
			.post()
			.then()
			.statusCode(200)
			//FIXME Why is true != true?!?!?!?!?
			//.body("data.updateUserById", equalTo(true.toString()))
		
		getUserByUsername(username)!!
			.statusCode(200)
			.body("data.userById.username", equalTo(username))
			.body("data.userById.email", equalTo(updatedEmail))
			.body("data.userById.name", equalTo(updatedName))
			.body("data.userById.dateOfBirth", equalTo(dateOfBirth))
	}
}