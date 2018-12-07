package no.ecm.authentication.service

import no.ecm.authentication.model.entity.UserEntity
import no.ecm.authentication.repository.AuthenticationRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class AuthenticationService(
        private val userCrud: AuthenticationRepository,
        private val passwordEncoder: PasswordEncoder
){


    fun createUser(username: String, password: String, roles: Set<String> = setOf()) : Boolean{

        try {
            val hash = passwordEncoder.encode(password)

            if (userCrud.existsById(username)) {
                return false
            }

            val user = UserEntity(username, hash, roles.map{"ROLE_$it"}.toSet())

            userCrud.save(user)

            return true
        } catch (e: Exception){
            return false
        }
    }

}