package no.ecm.movie.repository

import no.ecm.movie.model.entity.Genre
import no.ecm.movie.model.entity.Movie
import no.ecm.movie.model.entity.NowPlaying
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import javax.annotation.PostConstruct

@Component
class DefaultData(
        private val movieRepository: MovieRepository,
        private val genreRepository: GenreRepository,
        private val nowPlayingRepository: NowPlayingRepository
) {



    @PostConstruct
    fun createDefault(){

        val genre1 = Genre(name = "action")

        genreRepository.save(genre1)

        val movie1 = Movie(
                movieName = "Die Hard 1",
                posterURL = "http://blabla.io",
                movieDuration = 93,
                ageLimit = 15,
                genre = mutableSetOf(genre1))

        movieRepository.save(movie1)

        val nowPlaying1 = NowPlaying(
                movie =
                        movie1,
                        timeWhenMoviePlay = ZonedDateTime.now(),
                        freeSeats = mutableSetOf("a1", "a2", "b1")
        )

        nowPlayingRepository.save(nowPlaying1)

        val genreres = genreRepository.findByName(genre1.name!!).first()

        genreres.movies = mutableSetOf(movie1)

        val movieRes = movieRepository.findBymovieName(movie1.movieName!!)

        movieRes.forEach { print(it.genre.size) }
    }
}