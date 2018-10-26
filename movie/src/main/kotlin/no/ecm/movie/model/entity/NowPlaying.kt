package no.ecm.movie.model.entity

import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class NowPlaying (


        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:OneToOne(mappedBy = "nowPlaying") // cascade ??
        var movie: Movie? = null,

        @get:NotNull
        var timeWhenMoviePlay: ZonedDateTime,

        @get:ElementCollection
        var freeSeats: MutableSet<String> = mutableSetOf()
)