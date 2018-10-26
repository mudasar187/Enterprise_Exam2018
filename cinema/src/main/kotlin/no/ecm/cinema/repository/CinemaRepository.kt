package no.ecm.cinema.repository

import no.ecm.cinema.model.entity.Cinema
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CinemaRepository : CrudRepository<Cinema, Long>{

    fun findByName(name: String): Iterable<Cinema>
}