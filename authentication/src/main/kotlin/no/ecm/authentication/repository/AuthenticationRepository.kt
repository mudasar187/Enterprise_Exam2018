package no.ecm.authentication.repository

import no.ecm.authentication.model.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthenticationRepository: CrudRepository<UserEntity, String> {

}