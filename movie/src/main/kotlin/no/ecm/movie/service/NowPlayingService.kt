package no.ecm.movie.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson
import no.ecm.movie.model.converter.NowPlayingConverter
import no.ecm.movie.model.entity.NowPlaying
import no.ecm.movie.repository.NowPlayingRepository
import no.ecm.utils.converter.ConvertionHandler.Companion.convertTimeStampToZonedTimeDate
import no.ecm.utils.dto.movie.NowPlayingDto
import no.ecm.utils.exception.ConflictException
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

        if (!dto.id.isNullOrEmpty()){
            handleIllegalField("id")
        }
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

        val response : ResponseEntity<RoomResponse> = try {
            restTemplate.getForEntity(
                    "$cinemaPath/cinemas/${dto.cinemaId}/rooms/${dto.roomId}",
                    RoomResponse::class.java)
        } catch (e : HttpClientErrorException){
            val body = Gson().fromJson(e.responseBodyAsString, RoomResponse::class.java)
            throw UserInputValidationException(body.message!!, body.code!!)
        } catch (e : HttpServerErrorException){
            val body = Gson().fromJson(e.responseBodyAsString, RoomResponse::class.java)
            //FIXME lage en internal server
            throw UserInputValidationException(body.message!!, body.code!!)
        }

        if (response.body == null){
            throw Exception("empty body")
        }

        val nowPlaying = NowPlayingConverter.dtoToEntity(dto)
        nowPlaying.movie = movieService.getMovie(dto.movieDto!!.id)
        nowPlaying.freeSeats = response.body.data!!.list.first().seats!!.toMutableSet()

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

    private fun validateNowPlayingDto(dto: NowPlayingDto) {
        when {
            dto.movieDto == null -> handleMissingField("movieDto")
            dto.movieDto!!.id.isNullOrBlank() -> handleMissingField("movie id")
            dto.roomId.isNullOrBlank() -> handleMissingField("roomId")
            //dto.seats == null  -> handleMissingField("seats")
            dto.time.isNullOrBlank() -> handleMissingField("time")
        }
    }
}