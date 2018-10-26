package no.ecm.cinema.model.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
class Cinema(

        @get:Id @get:GeneratedValue
        var id: Long? = null,

        @get:NotBlank @get:Size(max = 128)
        var name: String,

        @get:NotBlank @get:Size(max = 128)
        var location: String? = null,

        @get:OneToMany(mappedBy = "cinema", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        var rooms: MutableSet<Room> = mutableSetOf()
)