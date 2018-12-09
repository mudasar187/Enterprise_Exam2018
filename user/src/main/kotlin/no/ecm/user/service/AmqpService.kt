package no.ecm.user.service

import no.ecm.utils.dto.user.UserDto
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class AmqpService(
        private var userService: UserService
) {

    // As anonymous queues have random names, we need to resolve them at runtime
    @RabbitListener(queues = ["#{queue.name}"])
    fun receiver(dto: UserDto) {
        println("::::::::::::::::::::::::::::::::")
        println(dto)
        println(dto.username)
        userService.createUser(dto)
    }
}