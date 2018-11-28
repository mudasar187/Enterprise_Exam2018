package no.ecm.order.controller

import com.sun.jndi.toolkit.url.Uri
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.order.model.converter.CouponConverter
import no.ecm.order.service.CouponService
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.hal.HalLinkGenerator
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@Api(value = "/coupons", description = "API for Coupon entity")
@RequestMapping(
	path = ["/coupons"],
	produces = [MediaType.APPLICATION_JSON_UTF8_VALUE]
)
@RestController
class CouponController {
	
	@Autowired
	private lateinit var service: CouponService
	
	@ApiOperation("Get coupon")
	@GetMapping
	fun get(
		@ApiParam("Code of the coupon to be returned")
		@RequestParam("code", required = false)
		code: String?,
		//
		@ApiParam("Offset in the list of coupons")
		@RequestParam("offset", defaultValue = "0")
		offset: Int,
		//
		@ApiParam("Limit of coupons in a single retrieved page")
		@RequestParam("limit", defaultValue = "10")
		limit: Int
	): ResponseEntity<WrappedResponse<CouponDto>> {
		
		val couponResultList = service.get(code, null, offset, limit)
		
		val builder = UriComponentsBuilder.fromPath("/coupons")
		if (!code.isNullOrBlank()){
			builder.queryParam("code", code)
		}
		
		val pageDto = CouponConverter.dtoListToPageDto(couponResultList, offset, limit)
		return HalLinkGenerator<CouponDto>().generateHalLinks(couponResultList, pageDto, builder, limit, offset)
		
		
		//return service.get(code, null, offset, limit)
	}
	
	@ApiOperation("Get a coupon by its ID")
	@GetMapping(path = ["/{id}"])
	fun getById(
		@ApiParam("Id of the coupon to be returned")
		@PathVariable("id", required = true)
		id: String,
		//
		@ApiParam("Offset in the list of coupons")
		@RequestParam("offset", defaultValue = "0")
		offset: Int,
		//
		@ApiParam("Limit of coupons in a single retrieved page")
		@RequestParam("limit", defaultValue = "10")
		limit: Int
	): ResponseEntity<WrappedResponse<CouponDto>> {
		
		val couponResultList = service.get(null, id, offset, limit)
		
		val builder = UriComponentsBuilder.fromPath("/coupons")
		builder.queryParam("id", id)
		
		val pageDto = CouponConverter.dtoListToPageDto(couponResultList, offset, limit)
		return HalLinkGenerator<CouponDto>().generateHalLinks(couponResultList, pageDto, builder, limit, offset)
		
		//return service.get(null, id, offset, limit)
	}
	
	@ApiOperation("Create a new coupon")
	@PostMapping(consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
	fun createCoupon(
		@ApiParam("Dto of a coupon: code, description, expireAt")
		@RequestBody dto: CouponDto
	): ResponseEntity<WrappedResponse<CouponDto>> {
		
		val returnId = service.create(dto)
		
		return ResponseEntity.status(201).body(
			ResponseDto(
				code = 201,
				page = PageDto(list = mutableListOf(CouponDto(id = returnId)))
			).validated()
		)
	}
	
	@ApiOperation("Uppdate all info for a given coupon")
	@PutMapping("/{id}")
	fun updateCoupon(@ApiParam("Id of the coupon to be updated")
					@PathVariable("id", required = true)
					id: String,
					//
					@ApiParam("The updated couponDto")
					@RequestBody
					updatedCouponDto: CouponDto
	): ResponseEntity<WrappedResponse<CouponDto>> {
		
		val returnId = service.put(id, updatedCouponDto)
		
		return ResponseEntity.status(201).body(
			ResponseDto(
				code = 201,
				page = PageDto(list = mutableListOf(CouponDto(id = returnId)))
			).validated()
		)
	}
	
	@ApiOperation("Delete a coupon with the given paramId")
	@DeleteMapping(path = ["/{id}"])
	fun deleteCoupon(@ApiParam("paramId of coupon")
					  @PathVariable("id", required = true)
					  paramId: String
	): ResponseEntity<WrappedResponse<CouponDto>> {
		
		val returnId = service.delete(paramId)
		
		return ResponseEntity.status(204).body(
			ResponseDto<CouponDto>(
				code = 204,
				message = "Coupon with paramId: $returnId successfully deleted"
			).validated()
		)
	}
	
	/*
		api/coupons (under invoice modul men egen url) Enkel (MARKER) (Skriver tester selv)
		GET -> alle coupons
			?code=code -> henter coupons
		GET /{id} -> henter coupon basert på id
		POST -> Opprette coupon
		DELETE /{id} -> Slette en invoice
	*/
}