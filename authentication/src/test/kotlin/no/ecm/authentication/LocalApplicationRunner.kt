//package no.ecm.authentication
//
//import org.junit.Assume
//import org.junit.BeforeClass
//import org.junit.ClassRule
//import org.springframework.boot.SpringApplication
//import org.springframework.boot.autoconfigure.SpringBootApplication
//import org.springframework.boot.test.util.TestPropertyValues
//import org.springframework.context.ApplicationContextInitializer
//import org.springframework.context.ConfigurableApplicationContext
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.context.ContextConfiguration
//import org.testcontainers.containers.GenericContainer
//
//@ActiveProfiles("test")
//@SpringBootApplication
//@ContextConfiguration(initializers = [(LocalApplicationRunner.Companion.Initializer::class)])
//class LocalApplicationRunner {
//    companion object {
//        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
//
//        /*
//            Here, going to use an actual Redis instance started in Docker
//         */
//
//        @ClassRule
//        @JvmField
//        val redis = KGenericContainer("redis:latest").withExposedPorts(6379)
//
//        @ClassRule
//        @JvmField
//        val rabbitMQ = KGenericContainer("rabbitmq:3").withExposedPorts(5672)
//
//
//        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
//            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
//
//                val redisHost = redis.containerIpAddress
//                val redisPort = redis.getMappedPort(6379)
//
//                val rabbitHost = rabbitMQ.containerIpAddress
//                val rabbitPort = rabbitMQ.getMappedPort(5672)
//
//
//
//                TestPropertyValues
//                        .of("spring.redis.host=$redisHost", "spring.redis.port=$redisPort",
//                                "spring.rabbitmq.host=$rabbitHost", "spring.rabbitmq.port=$rabbitPort")
//                        .applyTo(configurableApplicationContext.environment)
//
//            }
//        }
//    }
//}
//
//fun main(args: Array<String>) {
//    SpringApplication.run(LocalApplicationRunner::class.java, "--spring.profiles.active=test")
//}