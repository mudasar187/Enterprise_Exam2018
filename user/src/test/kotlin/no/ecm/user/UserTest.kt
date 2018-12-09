package no.ecm.user

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.equalTo
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
		
		given().auth().basic("admin", "admin")
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(createQuery)
			.post()
			.then()
			.statusCode(200)
			.body("data.createUser", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
	}
	
	@Test
	fun testCreateUserWithInvalidData() {
		val username = "frodobaggins"
		val email = "frodo@shire.no"
		val name = "Frodo Baggins"
		val dateOfBirth = "2018-11-04"
		
		val noUsername = """
                    { "query" :
                         "mutation{createUser(user:{username:null,dateOfBirth:\"$dateOfBirth\",name:\"$name\",email:\"$email\"})}"
                    }
                    """.trimIndent()
		
		val noDateOfBirth = """
                    { "query" :
                         "mutation{createUser(user:{username:\"$username\",dateOfBirth:null,name:\"$name\",email:\"$email\"})}"
                    }
                    """.trimIndent()
		
		val noName = """
                    { "query" :
                         "mutation{createUser(user:{username:\"$username\",dateOfBirth:\"$dateOfBirth\",name:null,email:\"$email\"})}"
                    }
                    """.trimIndent()
		
		val noEmail = """
                    { "query" :
                         "mutation{createUser(user:{username:\"$username\",dateOfBirth:\"$dateOfBirth\",name:\"$name\",email:null})}"
                    }
                    """.trimIndent()
		
		invalidUserQuery(noUsername)!!
			.body("data.createUser", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
		
		invalidUserQuery(noDateOfBirth)!!
			.body("data.createUser", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
		
		invalidUserQuery(noName)!!
			.body("data.createUser", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
		
		invalidUserQuery(noEmail)!!
			.body("data.createUser", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
		
	}
	
	
	@Test
	fun testGetNonExistingUser() {
		
		getUserByUsername("arhurdent42")!!
			.statusCode(200)
			.body("data.userById", equalTo(null))
	}
	
	@Test
	fun testDeleteUser() {
		
		val username = "frodo55"
		val email = "frodo@shire.no"
		val name = "Frodo Baggins"
		val dateOfBirth = "2018-11-04"
		
		val usernameRes = createUser(username, dateOfBirth, name, email)
		
		assertNotNull(usernameRes)
		assertEquals(username, usernameRes)
		
		getUserByUsername(username)!!
			.statusCode(200)
		
		val deleteQuery = """
                    { "query" :
                         "mutation{deleteUserById(id:\"$username\")}"
                    }
                    """.trimIndent()
		
		given().auth().basic("admin", "admin")
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(deleteQuery)
			.post()
			.then()
			.statusCode(200)
			.body("data.deleteUserById", CoreMatchers.notNullValue())
			.body("errors.message", CoreMatchers.nullValue())
	}
	
	@Test
	fun testDeleteNonExistingUser() {
		
		val deleteQuery = """
                    { "query" :
                         "mutation{deleteUserById(id: \"12346787654\")}"
                    }
                    """.trimIndent()
		
		given().auth().basic("admin", "admin")
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(deleteQuery)
			.post()
			.then()
			.statusCode(200)
			.body("data.deleteUserById", CoreMatchers.nullValue())
			.body("errors.message", CoreMatchers.notNullValue())
		
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
		
		invalidUserQuery(updateQuery)!!
			.body("data.updateUserById", CoreMatchers.notNullValue())
		
		getUserByUsername(username)!!
			.statusCode(200)
			.body("data.userById.username", equalTo(username))
			.body("data.userById.email", equalTo(updatedEmail))
			.body("data.userById.name", equalTo(updatedName))
			.body("data.userById.dateOfBirth", equalTo(dateOfBirth))
	}
	
	@Test
	fun testUpdateUserWithInvalidData() {
		
		val username = "frodo55"
		val email = "frodo@shire.no"
		val name = "Frodo Baggins"
		val dateOfBirth = "2018-11-04"
		
		val usernameRes = createUser(username, dateOfBirth, name, email)
		
		val updatedEmail = "bilbo@shire.no"
		val updatedName = "Bilbo Baggins"
		
		val noUsername = """
                    { "query" :
                         "mutation{updateUserById(id: null, name:\"$updatedName\", email:\"$updatedEmail\")}"
                    }
                    """.trimIndent()
		
		val noName = """
                    { "query" :
                         "mutation{updateUserById(id:\"$usernameRes\", name:null, email:\"$updatedEmail\")}"
                    }
                    """.trimIndent()
		
		val noEmail = """
                    { "query" :
                         "mutation{updateUserById(id:\"$usernameRes\", name:\"$updatedName\", email:null)}"
                    }
                    """.trimIndent()
		
		invalidUserQuery(noUsername)!!
			.body("data.updateUserById", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
			.extract().body().jsonPath().prettyPrint()
		
		invalidUserQuery(noName)!!
			.body("data.updateUserById", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
			.extract().body().jsonPath().prettyPrint()
		
		invalidUserQuery(noEmail)!!
			.body("data.updateUserById", CoreMatchers.nullValue())
			.body("errors.message[0]", CoreMatchers.notNullValue())
			.extract().body().jsonPath().prettyPrint()
		
	}
}