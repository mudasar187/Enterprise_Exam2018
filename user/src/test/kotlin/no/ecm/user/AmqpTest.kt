package no.ecm.user

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.user.repository.UserRepository
import no.ecm.utils.dto.user.UserDto
import org.awaitility.Awaitility.await
import org.hamcrest.Matchers
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.GenericContainer
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(AmqpTest.Companion.Initializer::class)])
@ActiveProfiles("test")
class AmqpTest {
	
	companion object {

		@BeforeClass
		@JvmStatic
		fun checkEnvironment(){

			/*
                Looks like currently some issues in running Docker-Compose on Travis
             */

			val travis = System.getProperty("TRAVIS") != null
			assumeTrue(!travis)
		}
		
		class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
		
		/*
			Here, going to use an actual Redis instance started in Docker
		 */
		
		@ClassRule
		@JvmField
		val redis = KGenericContainer("redis:latest").withExposedPorts(6379)
		
		@ClassRule
		@JvmField
		val rabbitMQ = KGenericContainer("rabbitmq:3").withExposedPorts(5672)
		
		
		class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
			override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
				
				val redisHost = redis.containerIpAddress
				val redisPort = redis.getMappedPort(6379)
				
				val rabbitHost = rabbitMQ.containerIpAddress
				val rabbitPort = rabbitMQ.getMappedPort(5672)
				
				TestPropertyValues
					.of("spring.redis.host=$redisHost", "spring.redis.port=$redisPort",
						"spring.rabbitmq.host=$rabbitHost", "spring.rabbitmq.port=$rabbitPort")
					.applyTo(configurableApplicationContext.environment)
				
			}
		}
	}
	
	@LocalServerPort
	protected var port = 0
	
	@Autowired
	private lateinit var template: RabbitTemplate
	@Autowired
	private lateinit var exchange: DirectExchange
	@Autowired
	private lateinit var userRepository: UserRepository
	
	@Before
	fun clean() {
		RestAssured.baseURI = "http://localhost"
		RestAssured.port = port
		RestAssured.basePath = "/graphql"
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
		userRepository.deleteAll()
		
		//val admin = UserEntity(username = "admin", dateOfBirth = LocalDate.now(), name = "Admin user", email = "admin@mail.com")
		//userRepository.save(admin)
	}
	
	@Test
	fun receiveAmqpMessageTest() {
		
		val username = "bar"
		val userDto = UserDto(username, "2018-12-24", "BarFoo", "bar@gmail.com")
		template.convertAndSend(exchange.name, "USER-REGISTRATION", userDto)
		
		val getQuery = """{userById(id: "$username") {username, email, name, dateOfBirth}}""".trimIndent()
		
		await().atMost(5, TimeUnit.SECONDS)
			.until {
				given()
					.auth().basic("bar", "123")
					.accept(ContentType.JSON)
					.contentType(ContentType.JSON)
					.queryParam("query", getQuery)
					.get()
					.then()
					.statusCode(200)
					.body("data.userById.username", Matchers.equalTo(username))
				true
			}
	}
}