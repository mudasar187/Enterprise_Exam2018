package no.ecm.order.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.order.model.converter.CouponConverter
import no.ecm.order.service.CouponService
import no.ecm.utils.cache.EtagHandler
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.hal.HalLinkGenerator
import no.ecm.utils.hal.PageDto
import no.ecm.utils.hal.PageDtoGenerator
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

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
	fun get(@ApiParam("Code of the coupon to be returned")
			@RequestParam("code", required = false)
			code: String?,
			
			@ApiParam("Offset in the list of coupons")
			@RequestParam("offset", defaultValue = "0")
			offset: Int,
			
			@ApiParam("Limit of coupons in a single retrieved page")
			@RequestParam("limit", defaultValue = "10")
			limit: Int
	): ResponseEntity<WrappedResponse<CouponDto>> {
		
		val couponResultList = service.get(code)
		
		val pageDto = PageDtoGenerator<CouponDto>().generatePageDto(couponResultList, offset, limit)
		val builder = UriComponentsBuilder.fromPath("/coupons")
		when {!code.isNullOrBlank() -> builder.queryParam("code", code) }
		
		return HalLinkGenerator<CouponDto>().generateHalLinks(couponResultList, pageDto, builder, limit, offset)
	}
	
	@ApiOperation("Get a coupon by its ID")
	@GetMapping(path = ["/{id}"])
	fun getById(@ApiParam("Id of the coupon to be returned")
				@PathVariable("id", required = true)
				paramId	: String,
	
				@ApiParam("Offset in the list of coupons")
				@RequestParam("offset", defaultValue = "0")
				offset: Int,
	
				@ApiParam("Limit of coupons in a single retrieved page")
				@RequestParam("limit", defaultValue = "10")
				limit: Int
	): ResponseEntity<WrappedResponse<CouponDto>> {
		
		val dto = CouponConverter.entityToDto(service.getById(paramId))
		val etag = dto.hashCode().toString()
		
		return ResponseEntity
			.status(200)
			.eTag(etag)
			.body(ResponseDto(
				code = 200,
				page = PageDto(mutableListOf(dto))
			))
	}
	
	@ApiOperation("Create a new coupon")
	@PostMapping(consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
	fun createCoupon(@ApiParam("Dto of a coupon: code, description, expireAt")
					 @RequestBody
					 dto: CouponDto
	): ResponseEntity<WrappedResponse<CouponDto>> {
		
		val returnId = service.create(dto)
		
		return ResponseEntity
			.status(201)
			.location(URI.create("/coupons/$returnId"))
			.body(
				ResponseDto(
					code = 201,
					page = PageDto(list = mutableListOf(CouponDto(id = returnId)))
				).validated()
		)
	}
	
	@ApiOperation("Update all info for a given coupon")
	@PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
	fun updateCoupon(@ApiParam("Id of the coupon to be updated")
					 @PathVariable("id", required = true)
					 id: String,
					 
					 @ApiParam("Content of ETag")
					 @RequestHeader("If-Match")
					 ifMatch: String?,
					 
					 @ApiParam("The updated couponDto")
					 @RequestBody
					 updatedCouponDto: CouponDto
	): ResponseEntity<Void> {
		
		val currentDto = CouponConverter.entityToDto(service.getById(id))
		EtagHandler<CouponDto>().validateEtags(currentDto, ifMatch)
		
		service.put(id, updatedCouponDto)
		return ResponseEntity.noContent().build()
	}
	
	@ApiOperation("Update a coupon with the given id")
	@PatchMapping(path = ["/{id}"], consumes = ["application/merge-patch+json"])
	fun patchDescription(@ApiParam("id of coupon")
						 @PathVariable("id", required = true)
						 id: String,
						 
						 @ApiParam("Content of ETag")
						 @RequestHeader("If-Match")
						 ifMatch: String?,
						 
						 @ApiParam("The partial patch (description only).")
						 @RequestBody jsonPatch: String
	): ResponseEntity<Void> {
		
		val currentDto = CouponConverter.entityToDto(service.getById(id))
		EtagHandler<CouponDto>().validateEtags(currentDto, ifMatch)
		
		service.patchDescription(id, jsonPatch)
		return ResponseEntity.noContent().build()
	}
	
	@ApiOperation("Delete a coupon with the given paramId")
	@DeleteMapping(path = ["/{id}"])
	fun deleteCoupon(@ApiParam("paramId of coupon")
					 @PathVariable("id", required = true)
					 paramId: String
	): ResponseEntity<WrappedResponse<CouponDto>> {
		
		val returnId = service.delete(paramId)
		
		return ResponseEntity.status(200).body(
			ResponseDto<CouponDto>(
				code = 200,
				message = "Coupon with paramId: $returnId successfully deleted"
			).validated()
		)
	}
}