package no.ecm.user.repository

import no.ecm.user.model.entity.User
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DefaultData(
        private var userRepository: UserRepository
) {

    @PostConstruct
    fun createData(){
        val user = User(username = "me", age = 20, name = "Me Bar", email = "me@me.com")
        userRepository.save(user)

        val userRes = userRepository.findById("me")
        println(userRes.get().email)

        val userResTwo = userRepository.findByEmail(email = "me@me.com")
        println(userResTwo.age)
    }
}