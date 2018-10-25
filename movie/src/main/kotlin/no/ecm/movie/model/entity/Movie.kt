package no.ecm.movie.model.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Movie (

        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:NotBlank
        var movieName: String? = null,

        @get:NotBlank
        var posterURL: String? = null,

        @ManyToMany(mappedBy = "movie", fetch = FetchType.EAGER) // cascade??
        var genre: MutableSet<Genre> = mutableSetOf(),

        @get:NotNull
        var movieDuration: Int,

        @get:NotNull
        var ageLimit: Int? = null,

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "movie_id")
        var nowPlaying: NowPlaying
)