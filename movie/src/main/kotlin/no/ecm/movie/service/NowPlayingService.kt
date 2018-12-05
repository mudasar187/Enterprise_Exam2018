package no.ecm.movie.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson
import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.exception.HystrixBadRequestException
import com.netflix.hystrix.exception.HystrixRuntimeException
import no.ecm.movie.model.converter.NowPlayingConverter
import no.ecm.movie.model.entity.NowPlaying
import no.ecm.movie.repository.NowPlayingRepository
import no.ecm.utils.converter.ConvertionHandler.Companion.convertTimeStampToZonedTimeDate
import no.ecm.utils.dto.cinema.RoomDto
import no.ecm.utils.dto.movie.NowPlayingDto
import no.ecm.utils.exception.ConflictException
import no.ecm.utils.exception.InternalException
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.messages.ExceptionMessages.Companion.inputFilterInvalid
import no.ecm.utils.messages.ExceptionMessages.Companion.notFoundMessage
import no.ecm.utils.messages.InfoMessages
import no.ecm.utils.response.RoomResponse
import no.ecm.utils.validation.ValidationHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class NowPlayingService(
        private var nowPlayingRepository: NowPlayingRepository,
        private var movieService: MovieService,
        private var restTemplate: RestTemplate
) {

    @Value("\${cinemaService}")
    private lateinit var cinemaPath : String

    val logger = logger<NowPlayingService>()


    fun find(title: String?, date: String?): MutableList<NowPlayingDto> {

        val nowPlaying = if (!title.isNullOrBlank() && date != null){
            logger.warn(inputFilterInvalid())
            throw UserInputValidationException(ExceptionMessages.inputFilterInvalid())
        }else if (!title.isNullOrBlank()){
            nowPlayingRepository.findAllByMovie_TitleContainsIgnoreCase(title!!).toMutableList()
        }else if (date != null){
            val start = convertTimeStampToZonedTimeDate(
                    ValidationHandler.validateTimeFormat("$date 00:00:00.000000"))
            val end = convertTimeStampToZonedTimeDate(
                    ValidationHandler.validateTimeFormat("$date 23:59:59.000000"))

            nowPlayingRepository.findAllByTimeWhenMoviePlayBetweenOrderByTimeWhenMoviePlayAsc(start!!, end!!)
                    .toMutableList()

        } else {
            nowPlayingRepository.findAll().toMutableList()
        }

        return NowPlayingConverter.entityListToDtoList(nowPlaying)
    }

    fun getNowPlayingById(paramId: String?): NowPlaying {

        val id = checkIfNowPlayingExists(paramId)

        return nowPlayingRepository.findById(id).get()
    }

    fun createNowPlaying(dto: NowPlayingDto): NowPlayingDto {
        validateNowPlayingDto(dto)

        if (dto.seats != null) handleIllegalField("seats")
        if (!dto.id.isNullOrEmpty()) handleIllegalField("id")

        val time = convertTimeStampToZonedTimeDate(
                ValidationHandler.validateTimeFormat("${dto.time}.000000"))

        if (nowPlayingRepository.existsByMovie_IdAndRoomIdAndTimeWhenMoviePlay(dto.movieDto!!.id!!.toLong(), dto.roomId!!.toLong(), time!!)){
            val errorMsg = (ExceptionMessages.resourceAlreadyExists("Now Playing",
                    "movieId, roomId and time",
                    "${dto.movieDto!!.id!!.toLong()}, " +
                            "${dto.roomId!!.toLong()} and $time"))
            logger.warn(errorMsg)
            throw ConflictException(errorMsg)
        }

        val response = CallGetFromCinema(dto).execute()

        if (response.data == null){
            throw InternalException(response.message!!, response.code!!)
        }

        validateRoomResponse(response.data!!.list.first())

        val nowPlaying = NowPlayingConverter.dtoToEntity(dto)
        nowPlaying.movie = movieService.getMovie(dto.movieDto!!.id)
        nowPlaying.freeSeats = response.data!!.list.first().seats!!.toMutableSet()

        val id = nowPlayingRepository.save(nowPlaying).id.toString()
        logger.info(InfoMessages.entityCreatedSuccessfully("Now Playing", id))
        return NowPlayingDto(id)

    }


    fun patchNowPlaying(stringId: String, body: String) {
        val id = ValidationHandler.validateId(stringId, "id")

        checkIfNowPlayingExists(stringId)

        val jackson = ObjectMapper()

        val jsonNode: JsonNode

        try {
            jsonNode = jackson.readValue(body, JsonNode::class.java)
        } catch (e: Exception) {
            val errorMsg = ExceptionMessages.invalidJsonFormat()
            logger.warn(errorMsg)
            throw UserInputValidationException(errorMsg)
        }

        val nowPlaying = getNowPlayingById(stringId)

        when {
            jsonNode.has("id") -> {
                handleIllegalField("id")
            }
            jsonNode.has("movieDto") -> {
                handleIllegalField("movieDto")
            }
            !jsonNode.has("seats") -> {
                handleMissingField("seats")
            }
            jsonNode.has("seats") -> {
                val seats = jsonNode.get("seats")
                when {
                    seats.isNull -> nowPlaying.freeSeats = mutableSetOf()
                    seats.isArray -> {
                        val mapper = jacksonObjectMapper()
                        val newSeats: Set<String> = mapper.readValue(seats.toString())
                        nowPlaying.freeSeats = newSeats.toMutableSet()
                    }
                    else -> handleUnableToParse("Now Playing")
                }
            }
        }

        nowPlayingRepository.save(nowPlaying)
        logger.info(InfoMessages.entitySuccessfullyUpdated("Now Playing", nowPlaying.id.toString()))
    }


    fun deleteById(paramId: String?): String? {

        val id = ValidationHandler.validateId(paramId, "id")

        val nowPlaying = getNowPlayingById(id.toString())

        nowPlaying.movie!!.nowPlaying.remove(nowPlaying)

        nowPlayingRepository.deleteById(id)
        val infoMsg = InfoMessages.entitySuccessfullyDeleted("now playing", "$id")
        logger.info(infoMsg)

        return id.toString()
    }

    private fun checkIfNowPlayingExists(stringId: String?): Long {

        val id = ValidationHandler.validateId(stringId, "id")

        if(!nowPlayingRepository.existsById(id)) {
            val erroMsg = notFoundMessage("Now Playing", "id", "$id")
            logger.warn(erroMsg)
            throw NotFoundException(erroMsg)
        }

        return id
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

    private fun validateRoomResponse(roomDto: RoomDto){

    }

    private fun validateNowPlayingDto(dto: NowPlayingDto) {
        when {
            dto.movieDto == null -> handleMissingField("movieDto")
            dto.movieDto!!.id.isNullOrBlank() -> handleMissingField("movie id")
            dto.roomId.isNullOrBlank() -> handleMissingField("roomId")
            //dto.seats == null  -> handleMissingField("seats")
            dto.time.isNullOrBlank() -> handleMissingField("time")
        }
    }

    private inner class CallGetFromCinema(private val dto: NowPlayingDto)
        : HystrixCommand<RoomResponse>(HystrixCommandGroupKey.Factory.asKey("Interactions with Cinema")) {


        override fun run(): RoomResponse {

            /*
                Note: this synchronous call could fail (and so throw an exception),
                or even just taking a long while (if server is under heavy load)
             */

            // val uri = URI(cinemaPath)

            val response : ResponseEntity<RoomResponse> = try {
                restTemplate.getForEntity(
                        "$cinemaPath/cinemas/${dto.cinemaId}/rooms/${dto.roomId}",
                        RoomResponse::class.java)
            } catch (e : HttpClientErrorException){
                val body = Gson().fromJson(e.responseBodyAsString, RoomResponse::class.java)
                throw HystrixBadRequestException(body.message!!, UserInputValidationException(message = body.message!!, httpCode = body.code!!))
                //UserInputValidationException(body.message!!, body.code!!)
            }

            return response.body!!
        }




        override fun getFallback(): RoomResponse {

            println(":::::::::: " + executionEvents)
            println("\n")
            println(failedExecutionException)

            if(failedExecutionException is HttpServerErrorException) {

            }



            //this is what is returned in case of exceptions or timeouts
            return RoomResponse(code = 503, message = "Tester hva jeg får her")
        }
    }
}