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
        val jondoe = User(username = "jondoe", age = 20, name = "Jon Doe", email = "jondoe@mail.com")
        val foobar = User(username = "foobar", age = 18, name = "Foo Bar", email = "foobar@mail.com")
        val farcar = User(username = "farcar", age = 45, name = "Far Car", email = "farcar@mail.com")

        userRepository.saveAll(mutableListOf(jondoe, foobar, farcar))

        val userRes = userRepository.findById("jondoe")
        println(userRes.get().email)

        val userResTwo = userRepository.findByEmail(email = "jondoe@mail.com")
        println(userResTwo.age)
    }
}