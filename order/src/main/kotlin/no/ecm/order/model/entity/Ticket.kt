package no.ecm.order.model.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Ticket (

        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:NotNull
        var price: Int? = null,

        @get:NotBlank
        var seatnumber: String? = null,

        @get:ManyToOne(fetch = FetchType.EAGER)
        @get:JoinColumn(name = "order_id")
        var order: Order? = null
)