package no.ecm.user

import org.springframework.amqp.core.*
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EntityScan(basePackages = ["no.ecm.user"])
class UserApplicationConfig {

    @Bean
    fun direct(): DirectExchange {
        return DirectExchange("user-registration.direct")
    }

    @Bean
    fun queue() : Queue {
        return AnonymousQueue()
    }

    @Bean
    fun binding_INFO(direct: DirectExchange, queue: Queue): Binding {
        return BindingBuilder
                .bind(queue)
                .to(direct)
                .with("INFO")
    }
}