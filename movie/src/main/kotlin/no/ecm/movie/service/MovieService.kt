package no.ecm.movie.service

import no.ecm.movie.repository.MovieRepository
import org.springframework.stereotype.Service

@Service
class MovieService (
        private var movieRepository: MovieRepository
){
}