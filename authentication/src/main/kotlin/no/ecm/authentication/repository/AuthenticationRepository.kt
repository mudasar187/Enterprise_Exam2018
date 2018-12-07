package no.ecm.authentication.repository

import no.ecm.authentication.model.entity.Authentication
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthenticationRepository: CrudRepository<Authentication, String> {

}