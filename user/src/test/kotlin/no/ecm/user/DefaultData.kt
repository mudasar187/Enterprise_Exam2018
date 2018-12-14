package no.ecm.user

import no.ecm.user.model.entity.UserEntity
import no.ecm.user.repository.UserRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDate
import javax.annotation.PostConstruct

@Component
@Profile("test")
class DefaultData(
        private var userRepository: UserRepository
) {

    @PostConstruct
    fun createData(){
        val jondoe = UserEntity(username = "jondoe", dateOfBirth = LocalDate.now() , name = "Jon Doe", email = "jondoe@mail.com")
        val foobar = UserEntity(username = "foobar", dateOfBirth = LocalDate.now(), name = "Foo Bar", email = "foobar@mail.com")
        val farcar = UserEntity(username = "farcar", dateOfBirth = LocalDate.now(), name = "Far Car", email = "farcar@mail.com")
        val admin = UserEntity(username = "admin", dateOfBirth = LocalDate.now(), name = "Admin user", email = "admin@mail.com")

        userRepository.saveAll(mutableListOf(jondoe, foobar, farcar, admin))

        val userRes = userRepository.findById("jondoe")
        println(userRes.get().email)

        val userResTwo = userRepository.findByEmail(email = "jondoe@mail.com")
        println(userResTwo.dateOfBirth)
    }
}