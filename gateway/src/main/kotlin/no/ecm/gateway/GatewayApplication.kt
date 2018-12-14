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
    
    // Gateway seems to strip requests for headers. This is a known problem and there has been creates a Issue on github for this:
    // https://github.com/spring-cloud/spring-cloud-gateway/issues/112
    
    // solution for for this is found on this issue comment:
    // https://github.com/spring-cloud/spring-cloud-gateway/issues/229#issuecomment-394132177
    // there is also need for a change in application.yml file, see this file for comments and link
    
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