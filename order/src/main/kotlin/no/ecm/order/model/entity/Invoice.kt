package no.ecm.order.model.entity

import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Invoice (

        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL]) //(mappedBy = "invoice", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        var tickets: MutableSet<Ticket> = mutableSetOf(),

        @get:NotBlank
        var username : String,

        @get:NotNull
        var orderDate: ZonedDateTime,

        @get:ManyToOne(fetch = FetchType.LAZY)
        @get:JoinColumn(name = "coupon_id")
        var coupon: Coupon? = null,

        @get:NotNull
        var nowPlayingId: Long? = null,

        @get:NotNull
        var paid: Boolean = false,

        @get:NotNull
        var totalPrice: Double? = null
)