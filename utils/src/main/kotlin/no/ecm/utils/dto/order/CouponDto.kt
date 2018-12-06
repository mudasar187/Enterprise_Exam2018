package no.ecm.utils.dto.order

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.time.ZonedDateTime

@ApiModel("DTO representing an Coupon")
data class CouponDto(
	
	@ApiModelProperty("The id of an Coupon")
	var id: String? = null,
	
	@ApiModelProperty("The code of an Coupon")
	var code: String? = null,
	
	@ApiModelProperty("The description of a Coupon")
	var description: String? = null,
	
	@ApiModelProperty("The expiration date of a Coupon")
	var expireAt: String? = null,
	
	@ApiModelProperty("The percentage of price reduction")
	var percentage: Int? = null



)