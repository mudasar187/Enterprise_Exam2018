package no.ecm.authentication.service

import no.ecm.utils.dto.user.UserDto
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

    fun send(userDto: UserDto, key: String) {
        template.convertAndSend(direct.name, key, userDto)
    }

}