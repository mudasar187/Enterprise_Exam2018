package no.ecm.movie.controller

import io.swagger.annotations.Api
import no.ecm.movie.model.converter.MovieConverter
import no.ecm.movie.model.entity.Movie
import no.ecm.movie.repository.MovieRepository
import no.ecm.utils.dto.movie.MovieDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@Api(value = "/movies", description = "API for movie entity")
@RequestMapping(
        path = ["/movies"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class MovieController (
){
    
    
    
    
    @Value("\${cinemaService}")
    private lateinit var cinemaHost : String

    @GetMapping(produces = ["application/json"])
    fun get(): ResponseEntity<String> {
        
        val res = "123453465786"
        
        val etag = res.hashCode().toString()
        
        return ResponseEntity.status(200).eTag(etag).body(res)
        
    }
    
    @GetMapping(
        produces = ["application/json"],
        path = ["/2"])
    fun get2() : String {
        return "{}"
    }
}