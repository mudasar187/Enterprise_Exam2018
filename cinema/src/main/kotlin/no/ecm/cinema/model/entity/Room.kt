package no.ecm.cinema.model.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Room (

        @get:Id @get:GeneratedValue
        var id: Long? = null,

        @get:NotBlank @get:Size(max = 128)
        var name: String,

        @get:ElementCollection
        @get:NotNull
        var seats : MutableSet<String>,

//        @get:ManyToOne(fetch = FetchType.EAGER)
//        @get:JoinColumn(name = "cinema_id")
//        var cinema: Cinema? = null

        @get:NotNull
        var cinemaId: Long
)