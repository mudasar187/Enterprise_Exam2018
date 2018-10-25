package no.ecm.admin.model.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
class Admin (


        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:NotNull
        var userName: String,

        @get:NotNull
        var password: String
)