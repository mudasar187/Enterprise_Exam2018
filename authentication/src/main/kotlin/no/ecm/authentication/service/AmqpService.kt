package no.ecm.authentication.service

import no.ecm.utils.dto.auth.RegistrationDto
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AmqpService {


    @Autowired
    private lateinit var template: RabbitTemplate

    @Autowired
    private lateinit var direct: DirectExchange

    fun send(registrationDto: RegistrationDto, key: String) {
        template.convertAndSend(direct.name, key, registrationDto)
    }

}