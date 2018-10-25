package no.ecm.order.model.entity

import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Coupon(

        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:NotBlank
        @get:Size(min = 5, max = 128)
        var code: String,

        @get:NotBlank
        @get:Size(max = 512)
        var description: String,

        @get:NotNull
        var expireAt: ZonedDateTime,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "coupon_id")
        var order: Order? = null
)