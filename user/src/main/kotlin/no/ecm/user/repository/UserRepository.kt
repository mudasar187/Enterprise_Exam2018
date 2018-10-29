package no.ecm.user.repository

import no.ecm.user.model.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, String> {

    fun findByEmail(email: String): User
}