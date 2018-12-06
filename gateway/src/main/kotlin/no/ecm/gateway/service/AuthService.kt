package no.ecm.gateway.service

import no.ecm.gateway.model.entity.Auth
import no.ecm.gateway.repository.AuthRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class AuthService(
        private val userCrud: AuthRepository,
        private val passwordEncoder: PasswordEncoder
){


    fun createUser(username: String, password: String, roles: Set<String> = setOf()) : Boolean{

        try {
            val hash = passwordEncoder.encode(password)

            if (userCrud.existsById(username)) {
                return false
            }

            val user = Auth(username, hash, roles.map{"ROLE_$it"}.toSet())

            userCrud.save(user)

            return true
        } catch (e: Exception){
            return false
        }
    }

}
