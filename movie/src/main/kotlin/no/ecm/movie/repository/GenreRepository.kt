package no.ecm.movie.repository

import no.ecm.movie.model.entity.Genre
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GenreRepository : CrudRepository<Genre, Long> {

    fun findByName(name: String): Iterable<Genre>
}