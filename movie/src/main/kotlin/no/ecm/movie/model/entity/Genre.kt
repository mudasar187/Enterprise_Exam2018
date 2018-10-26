package no.ecm.movie.model.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Genre (

        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:NotBlank
        var name: String? = null,

        @get:ManyToMany(fetch = FetchType.EAGER)
        @get:JoinColumn(name = "movie_id")
        var movies: MutableSet<Movie> = mutableSetOf()
)