package no.ecm.order.service

import com.google.gson.Gson
import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.exception.HystrixBadRequestException
import no.ecm.order.model.converter.CouponConverter
import no.ecm.order.model.converter.InvoiceConverter
import no.ecm.order.model.converter.TicketConverter
import no.ecm.order.model.entity.Invoice
import no.ecm.order.repository.InvoiceRepository
import no.ecm.utils.dto.movie.NowPlayingDto
import no.ecm.utils.dto.order.InvoiceDto
import no.ecm.utils.exception.InternalException
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.messages.ExceptionMessages.Companion.invalidFieldCombination
import no.ecm.utils.messages.InfoMessages
import no.ecm.utils.messages.InfoMessages.Companion.entityCreatedSuccessfully
import no.ecm.utils.messages.InfoMessages.Companion.entitySuccessfullyUpdated
import no.ecm.utils.response.NowPlayingReponse
import no.ecm.utils.validation.ValidationHandler
import no.ecm.utils.validation.ValidationHandler.Companion.validateId
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class InvoiceService(
        private var invoiceRepository: InvoiceRepository,
        private var ticketService: TicketService,
        private var couponService: CouponService,
        private var restTemplate: RestTemplate
) {

    @Value("\${movieService}")
    private lateinit var moviePath : String
    
    @Value("\${ticketPrice}")
    private lateinit var ticketPrice: String

    val logger = logger<InvoiceService>()

    fun findInvoice(username: String?, nowPlayingId: String?, isPaid: Boolean?): MutableList<InvoiceDto> {

        val invoices = when {
            !username.isNullOrBlank() && !nowPlayingId.isNullOrBlank() && isPaid != null -> {
                val errorMsg = invalidFieldCombination("username, nowPlayingId and paid")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)}

            !username.isNullOrBlank() && !nowPlayingId.isNullOrBlank() -> invoiceRepository
                    .findAllByUsernameIgnoreCaseAndNowPlayingId(
                            username!!,
                            validateId(nowPlayingId, "nowPLayingId"))

            !username.isNullOrBlank() && isPaid != null -> {
                invoiceRepository.findAllByUsernameIgnoreCaseAndPaid(username!!, isPaid)}

            !username.isNullOrBlank() -> invoiceRepository.findAllByUsernameIgnoreCase(username!!).toMutableList()

            isPaid != null && !nowPlayingId.isNullOrBlank() -> invoiceRepository
                    .findAllByPaidAndNowPlayingId(
                            isPaid,
                            validateId(nowPlayingId, "nowPLayingId"))

            isPaid != null -> invoiceRepository.findAllByPaid(isPaid)

            !nowPlayingId.isNullOrBlank() -> {
                invoiceRepository.findAllByNowPlayingId(validateId(nowPlayingId, "nowPLayingId"))}

            else -> invoiceRepository.findAll().toMutableList()
        }
        return InvoiceConverter.entityListToDtoList(invoices)
    }

    fun findById(stringId: String?): Invoice {
        val id = checkFOrInvoiceInDatabase(stringId)

        return invoiceRepository.findById(id).get()
    }

    fun deleteById(stringId: String?): String? {

        val id = checkFOrInvoiceInDatabase(stringId)

        invoiceRepository.deleteById(id)
        val infoMsg = InfoMessages.entitySuccessfullyDeleted("invoice", "$id")
        logger.info(infoMsg)

        return id.toString()

    }
    
    fun createInvoice(dto: InvoiceDto): InvoiceDto {
        if (dto.id != null) {
            handleIllegalField("id")
        }
        
        validateInvoiceDto(dto)

        if (dto.couponCode != null && !dto.couponCode!!.id.isNullOrBlank()){
            val couponDto = couponService.get(null, dto.couponCode!!.id).first()
            val discount = (couponDto.percentage!!.toDouble() / 100.toDouble()) * (ticketPrice.toDouble() * dto.tickets!!.size.toDouble())
            
            dto.totalPrice = (ticketPrice.toDouble() * dto.tickets!!.size.toDouble()) - discount
            dto.couponCode = couponDto
        } else {
            dto.totalPrice = ticketPrice.toDouble() * dto.tickets!!.size.toDouble()
        }

        val response = CallGetFromMovieService(dto).execute()

        if (response.body!!.data == null){
            throw InternalException(response.body!!.message!!, response.body!!.code!!)
        }
        validateNowPlayingResponse(response.body!!)

        val nowPlayingDto = response.body!!.data!!.list.first()

        val seats = nowPlayingDto.seats!!.toMutableList()

        dto.tickets!!.forEach { seats.remove(it.seat) }


        val jsonBody = """{
                    "seats": ${seats.joinToString("\",\"", "[\"", "\"]")}
                    }""".trimMargin()

        val patchResponse = CallPatchToMovieService(jsonBody, nowPlayingDto.id!!, response.headers.eTag!!).execute()

        if (patchResponse.code == HttpStatus.INTERNAL_SERVER_ERROR.value()){
            throw InternalException("INTERNAL_SERVER_ERROR", patchResponse.code!!)
        } else if (patchResponse.code == HttpStatus.SERVICE_UNAVAILABLE.value()){
            throw InternalException("SERVICE_TEMPORARILY_UNAVAILABLE", patchResponse.code!!)
        }
        logger.info(entitySuccessfullyUpdated("NowPlaying", nowPlayingDto.id.toString()))

        val id = invoiceRepository.save(InvoiceConverter.dtoToEntity(dto)).id
        logger.info(entityCreatedSuccessfully("Invoice", id.toString()))

        try {
            dto.tickets!!.forEach {
                it.invoiceId = id.toString()
                ticketService.create(it)
            }
            val invoice = findById(id.toString())
            invoice.tickets = TicketConverter.dtoListToEntityList(dto.tickets!!).toMutableSet()

            if (dto.couponCode != null){
                invoice.coupon = couponService.getById(dto.couponCode!!.id)
            }
            invoiceRepository.save(invoice)
            logger.info(entityCreatedSuccessfully("Invoice", id.toString()))

        } catch (e : Exception){
            deleteById(id.toString())
            throw e
        }


        return InvoiceDto(id.toString())
    }

    private fun checkFOrInvoiceInDatabase(stringId: String?): Long {
        val id = ValidationHandler.validateId(stringId, "id")

        when {
            !invoiceRepository.existsById(id) -> {
                val errorMsg = ExceptionMessages.notFoundMessage("Invoice", "id", id.toString())
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
            else -> return id
        }

    }
    
    private fun validateInvoiceDto(dto: InvoiceDto) {
        when {
            dto.username.isNullOrBlank() -> handleMissingField("cusername")
            dto.orderDate.isNullOrBlank() -> handleMissingField("orderDate")
            dto.nowPlayingId.isNullOrBlank() -> handleMissingField("nowPlayingId")
            dto.tickets == null -> handleMissingField("tickets")
            dto.totalPrice != null -> handleIllegalField("totlPrice")
        }
    }

    private fun handleUnableToParse(fieldName: String){
        val errorMsg = ExceptionMessages.unableToParse(fieldName)
        logger.warn(errorMsg)
        throw UserInputValidationException(errorMsg)
    }

    private fun handleIllegalField(fieldName: String) {
        val errorMsg = ExceptionMessages.illegalParameter(fieldName)
        logger.warn(errorMsg)
        throw UserInputValidationException(errorMsg)
    }

    private fun handleMissingField(fieldName: String){
        val errorMsg = ExceptionMessages.missingRequiredField(fieldName)
        logger.warn(errorMsg)
        throw UserInputValidationException(errorMsg)
    }

    private fun validateNowPlayingResponse(nowPlayingResponse: NowPlayingReponse){
        when {
            nowPlayingResponse.data == null -> handleMissingField("data")
            nowPlayingResponse.data!!.list.isEmpty() -> handleMissingField("list")
            nowPlayingResponse.data!!.list.first().cinemaId.isNullOrBlank() -> handleMissingField("cinemaId")
            nowPlayingResponse.data!!.list.first().id.isNullOrBlank() -> handleMissingField("id")
            nowPlayingResponse.data!!.list.first().roomId.isNullOrBlank() -> handleMissingField("roomId")
            nowPlayingResponse.data!!.list.first().seats == null -> handleMissingField("seats is null")
            nowPlayingResponse.data!!.list.first().seats!!.isEmpty() -> handleMissingField("seats is empty")
        }
    }

    private inner class CallGetFromMovieService(private val dto: InvoiceDto)
        : HystrixCommand<ResponseEntity<NowPlayingReponse>>(HystrixCommandGroupKey.Factory.asKey("Getting Now Playing information from Movie service")) {

        override fun run(): ResponseEntity<NowPlayingReponse> {

            val response : ResponseEntity<NowPlayingReponse> = try {
                restTemplate.getForEntity(
                        "$moviePath/now-playing/${dto.nowPlayingId}",
                        NowPlayingReponse::class.java)
            } catch (e : HttpClientErrorException){
                val body = Gson().fromJson(e.responseBodyAsString, NowPlayingReponse::class.java)
                logger.warn(body.message)
                throw HystrixBadRequestException(body.message!!, UserInputValidationException(message = body.message!!, httpCode = body.code!!))
            }

            return response
        }

        override fun getFallback(): ResponseEntity<NowPlayingReponse> {

            logger.error("Critical error! Movie service crashed")
            logger.error("Circuit breaker status: $executionEvents")

            if(failedExecutionException is HttpServerErrorException) {
                return ResponseEntity.status(500).body(NowPlayingReponse(message = "INTERNAL_SERVER_ERROR", code = 500))
            }

            return ResponseEntity.status(503).body(NowPlayingReponse(message = "SERVICE_TEMPORARY_UNAVAILABLE", code = 503))
        }
    }

    private inner class CallPatchToMovieService(private val jsonPatchBody: String, private var nowPlayingId: String, private var eTag: String)
        : HystrixCommand<NowPlayingReponse>(HystrixCommandGroupKey.Factory.asKey("Removing seats from Now Playing in Movie service")) {

        override fun run(): NowPlayingReponse {
            val headers = HttpHeaders()
            headers.set("If-Match", eTag)
            headers.set("Content-Type", "application/merge-patch+json")

            val response : ResponseEntity<Void> = try {
                restTemplate.exchange(
                        "$moviePath/now-playing/$nowPlayingId",
                        HttpMethod.PATCH,
                        HttpEntity(jsonPatchBody, headers),
                        Void::class.java)
            } catch (e : HttpClientErrorException){
                val body = Gson().fromJson(e.responseBodyAsString, NowPlayingReponse::class.java)
                logger.warn(body.message)
                throw HystrixBadRequestException(body.message, UserInputValidationException(message = body.message!!, httpCode = body.code!!))
            }

            return NowPlayingReponse(response.statusCodeValue)
        }

        override fun getFallback(): NowPlayingReponse {

            logger.error("Critical error! Movie service not working properly")
            logger.error("Circuit breaker status: $executionEvents")

            if(failedExecutionException is HttpServerErrorException) {
                return NowPlayingReponse(message = "INTERNAL_SERVER_ERROR", code = 500)
            }

            return NowPlayingReponse(message = "SERVICE_TEMPORARILY_UNAVAILABLE", code = 503)
        }
    }
}