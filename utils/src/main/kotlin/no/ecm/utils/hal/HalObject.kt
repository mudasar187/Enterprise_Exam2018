package no.ecm.utils.hal

import io.swagger.annotations.ApiModelProperty

open class HalObject(

        @ApiModelProperty("HAL links")
        var _links: MutableMap<String, HalLink> = mutableMapOf()
)