package no.ecm.gateway.repository

import no.ecm.gateway.model.entity.Auth
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthRepository: CrudRepository<Auth, String> {

}