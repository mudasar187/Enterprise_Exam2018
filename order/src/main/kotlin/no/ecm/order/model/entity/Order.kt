package no.ecm.order.model.entity

import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Order (

        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @OneToMany(mappedBy = "order", cascade = [(CascadeType.ALL)], fetch = FetchType.EAGER)
        var tickets: MutableSet<Ticket> = mutableSetOf(),

        @get:NotBlank
        var username : String,

        @get:NotNull
        var orderDate: ZonedDateTime,

        @OneToOne(mappedBy = "order", fetch = FetchType.EAGER) // cascade ??
        var couponId: String? = null,

        @get:NotNull
        var nowPlayingId: Long? = null

)