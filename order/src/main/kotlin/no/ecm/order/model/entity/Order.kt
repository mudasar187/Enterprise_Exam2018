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

        @get:OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
        var tickets: MutableSet<Ticket> = mutableSetOf(),

        @get:NotBlank
        var username : String,

        @get:NotNull
        var orderDate: ZonedDateTime,

        @get:ManyToOne(fetch = FetchType.EAGER)
        @get:JoinColumn(name = "coupon_id")
        var coupon: Coupon? = null,


        @get:NotNull
        var nowPlayingId: Long? = null

)