package no.ecm.user.repository

import no.ecm.user.model.entity.User
import org.springframework.stereotype.Component
import java.time.LocalDate
import javax.annotation.PostConstruct

@Component
class DefaultData(
        private var userRepository: UserRepository
) {

    @PostConstruct
    fun createData(){
        val jondoe = User(username = "jondoe", dateOfBitrh = LocalDate.now() , name = "Jon Doe", email = "jondoe@mail.com")
        val foobar = User(username = "foobar", dateOfBitrh = LocalDate.now(), name = "Foo Bar", email = "foobar@mail.com")
        val farcar = User(username = "farcar", dateOfBitrh = LocalDate.now(), name = "Far Car", email = "farcar@mail.com")

        userRepository.saveAll(mutableListOf(jondoe, foobar, farcar))

        val userRes = userRepository.findById("jondoe")
        println(userRes.get().email)

        val userResTwo = userRepository.findByEmail(email = "jondoe@mail.com")
        println(userResTwo.dateOfBitrh)
    }
}