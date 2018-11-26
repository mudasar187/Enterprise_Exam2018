package no.ecm.movie.repository

import no.ecm.movie.model.entity.Genre
import no.ecm.movie.model.entity.Movie
import no.ecm.movie.model.entity.NowPlaying
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import javax.annotation.PostConstruct
import kotlin.math.log

@Component
class DefaultData(
        private val movieRepository: MovieRepository,
        private val genreRepository: GenreRepository,
        private val nowPlayingRepository: NowPlayingRepository
) {
    @PostConstruct
    fun createDefault(){

        val action = Genre(name = "Action")
        val adventure = Genre(name = "Adventure")
        val scifi = Genre(name = "Sci-Fi")
        
        genreRepository.saveAll(mutableListOf(action, adventure, scifi))

        val movie1 = Movie(null, "Inception", "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg",  mutableSetOf(action), 120, 13)
        val movie2 = Movie(null, "Isle of Dogs", "https://m.media-amazon.com/images/M/MV5BMTYyOTUwNjAxM15BMl5BanBnXkFtZTgwODcyMzE0NDM@._V1_SX300.jpg",  mutableSetOf(action), 90, 15)
        val movie3 = Movie(null, "Avengers: Infinity War", "https://m.media-amazon.com/images/M/MV5BMjMxNjY2MDU1OV5BMl5BanBnXkFtZTgwNzY1MTUwNTM@._V1_SX300.jpg",  mutableSetOf(adventure), 84, 18)
        val movie4 = Movie(null, "Batman: The Dark Knight", "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_SX300.jpg",  mutableSetOf(action, scifi), 105, 7)
        val movie5 = Movie(null, "The Hateful Eight", "https://m.media-amazon.com/images/M/MV5BMjA1MTc1NTg5NV5BMl5BanBnXkFtZTgwOTM2MDEzNzE@._V1_SX300.jpg",  mutableSetOf(scifi), 98, 15)
        val movie6 = Movie(null, "Django Unchained", "https://m.media-amazon.com/images/M/MV5BMjIyNTQ5NjQ1OV5BMl5BanBnXkFtZTcwODg1MDU4OA@@._V1_SX300.jpg",  mutableSetOf(scifi, adventure), 100, 10)
        val movie7 = Movie(null, "Star Wars: Episode IV - A New Hope", "https://m.media-amazon.com/images/M/MV5BNzVlY2MwMjktM2E4OS00Y2Y3LWE3ZjctYzhkZGM3YzA1ZWM2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg",  mutableSetOf(action, adventure), 85, 7)
        
        movieRepository.saveAll(mutableListOf(movie1, movie2, movie3, movie4, movie5, movie6, movie7))

        
        val nowPlaying1 = NowPlaying(null, movie1, 1, ZonedDateTime.now().plusDays(2), mutableSetOf("A1", "A2", "B1", "A3", "C2", "D4", "A7", "A5", "D2"))
        val nowPlaying2 = NowPlaying(null, movie1, 2, ZonedDateTime.now().plusDays(1), mutableSetOf("A1", "A2", "B1", "D4", "A7", "A5", "D2"))
        val nowPlaying3 = NowPlaying(null, movie1, 3, ZonedDateTime.now().plusDays(3), mutableSetOf("A2", "B1", "A3", "C2", "D4", "A7"))
        val nowPlaying4 = NowPlaying(null, movie1, 1, ZonedDateTime.now().plusDays(8), mutableSetOf("B1", "A3", "C2", "D4", "A7", "A5", "D2"))
        val nowPlaying5 = NowPlaying(null, movie1, 3, ZonedDateTime.now().plusDays(6), mutableSetOf("A2", "B1", "A3", "C2", "D4", "A7"))
        val nowPlaying6 = NowPlaying(null, movie1, 2, ZonedDateTime.now().plusDays(2), mutableSetOf("A1", "A2", "B1"))
        
        nowPlayingRepository.saveAll(mutableListOf(nowPlaying1, nowPlaying2, nowPlaying3, nowPlaying3, nowPlaying4, nowPlaying5, nowPlaying6))
        
        val actionGenre = genreRepository.findByName(action.name!!)
        val adventureGenre = genreRepository.findByName(adventure.name!!)
        val scifiGenre = genreRepository.findByName(scifi.name!!)
    
        actionGenre.movies.addAll(mutableListOf(movie1, movie2, movie4, movie7))
        adventureGenre.movies.addAll(mutableListOf(movie3, movie6, movie7))
        scifiGenre.movies.addAll(mutableListOf(movie4, movie5, movie6))
        
        genreRepository.saveAll(mutableListOf(actionGenre, adventureGenre, scifiGenre))
    }
}