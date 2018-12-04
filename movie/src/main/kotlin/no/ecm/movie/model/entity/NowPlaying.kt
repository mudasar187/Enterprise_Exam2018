package no.ecm.movie.model.entity

import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class NowPlaying (
        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:ManyToOne(fetch = FetchType.EAGER)
        @get:JoinColumn(name = "movie_id")
        var movie: Movie? = null,
        
        @get:NotNull
        var roomId: Long? = null,

        @get:NotNull
        var timeWhenMoviePlay: ZonedDateTime,

        @get:ElementCollection
        var freeSeats: MutableSet<String> = mutableSetOf()
)