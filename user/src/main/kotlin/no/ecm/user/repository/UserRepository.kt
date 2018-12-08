package no.ecm.user.repository

import no.ecm.user.model.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<UserEntity, String> {

    fun findByEmail(email: String): UserEntity
}