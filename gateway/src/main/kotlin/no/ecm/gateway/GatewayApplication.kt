package no.ecm.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.web.cors.CorsConfiguration
import java.util.*

@SpringBootApplication(scanBasePackages = ["no.ecm.gateway"])
@EnableDiscoveryClient
class GatewayApplication{
    @Bean
    fun corsConfiguration(routePredicateHandlerMapping: RoutePredicateHandlerMapping): CorsConfiguration {
        val corsConfiguration = CorsConfiguration().applyPermitDefaultValues()
        Arrays.asList(HttpMethod.OPTIONS, HttpMethod.PUT, HttpMethod.GET, HttpMethod.DELETE, HttpMethod.POST).forEach {
            m -> corsConfiguration.addAllowedMethod(m) }
        corsConfiguration.addAllowedOrigin("*")
        corsConfiguration.allowCredentials = true
        corsConfiguration.addAllowedHeader("*")
        routePredicateHandlerMapping.setCorsConfigurations(object : HashMap<String, CorsConfiguration>() {
            init {
                put("/**", corsConfiguration)
            }
        })
        return corsConfiguration
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(GatewayApplication::class.java, *args)
}