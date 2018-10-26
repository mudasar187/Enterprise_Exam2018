package no.ecm.movie.repository

import no.ecm.movie.model.entity.Movie
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository : CrudRepository<Movie, Long> {

    fun findBymovieName(movieName: String): Iterable<Movie>
}