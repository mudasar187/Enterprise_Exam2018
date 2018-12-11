package no.ecm.order.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.order.model.converter.InvoiceConverter
import no.ecm.order.service.InvoiceService
import no.ecm.utils.cache.EtagHandler
import no.ecm.utils.dto.order.InvoiceDto
import no.ecm.utils.hal.HalLinkGenerator
import no.ecm.utils.hal.PageDto
import no.ecm.utils.hal.PageDtoGenerator
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@Api(value = "/invoices", description = "API for order entity")
@RequestMapping(
	path = ["/invoices"],
	produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
@CrossOrigin(origins = ["http://localhost:8080"])
class InvoiceController(
		private var invoiceService: InvoiceService
) {
	
	@Value("\${movieService}")
	private lateinit var movieHost : String

	@ApiOperation("Get Invoices, possible filter by username")
	@GetMapping
	fun get(@ApiParam("Username of the customer for this invoice")
			@RequestParam("username", required = false)
			username : String?,

			@ApiParam("Now Playing Id of a invoice")
			@RequestParam("nowPlayingId", required = false)
			nowPlayingId : String?,

			@ApiParam("Boolean for if a invoice is paid")
			@RequestParam("paid", required = false)
			paid : Boolean?,

			@ApiParam("Offset in the list of invoices")
			@RequestParam("offset", defaultValue = "0")
			offset: Int,

			@ApiParam("Limit of invoices in a single retrieved page")
			@RequestParam("limit", defaultValue = "10")
			limit: Int): ResponseEntity<WrappedResponse<InvoiceDto>>{
		val invoiceDtos = invoiceService.findInvoice(username, nowPlayingId, paid)

		val builder = UriComponentsBuilder.fromPath("/invoices")

		if (!username.isNullOrBlank()) {
			builder.queryParam("username", username)
		}

		val pageDto = PageDtoGenerator<InvoiceDto>().generatePageDto(invoiceDtos, offset, limit)
		return HalLinkGenerator<InvoiceDto>().generateHalLinks(invoiceDtos, pageDto, builder, limit, offset)
	}

	@ApiOperation("Get an Invoice by the id")
	@GetMapping(path = ["/{id}"])
	fun getInvoice(
			@ApiParam("id of the Invoice")
			@PathVariable("id") id: String): ResponseEntity<WrappedResponse<InvoiceDto>> {

		val dto = InvoiceConverter.entityToDto(invoiceService.findById(id))
		val etag = EtagHandler<InvoiceDto>().generateEtag(dto = dto)

		return ResponseEntity
				.status(HttpStatus.OK.value())
				.eTag(etag)
				.body(
						ResponseDto(
								code = HttpStatus.OK.value(),
								page = PageDto(mutableListOf(dto))
						).validated()
				)
	}
	
	@ApiOperation("Create a new Invoice")
	@PostMapping()
	fun createInvoice(@ApiParam("Dto of the Invoice")
					  @RequestBody
					  invoiceDtoParam: InvoiceDto
	): ResponseEntity<WrappedResponse<InvoiceDto>> {
		
		val returnDto = invoiceService.createInvoice(invoiceDtoParam)
		
		return ResponseEntity
			.status(201)
			.body(ResponseDto(
				code = 201,
				page = PageDto(mutableListOf(returnDto))
			))
		
	}

	@ApiOperation("Delete a invoice by id")
	@DeleteMapping(path = ["/{id}"])
	fun deleteInvoiceById(
			@ApiParam("id of the invoice")
			@PathVariable("id")
			id: String
	): ResponseEntity<WrappedResponse<String?>> {
		return ResponseEntity.ok(
				ResponseDto(
						code = HttpStatus.OK.value(),
						page = PageDto(mutableListOf(invoiceService.deleteById(id)))
				).validated()
		)
	}
}