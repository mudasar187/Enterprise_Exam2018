package no.ecm.cinema.repository

import no.ecm.cinema.model.entity.Room
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoomRepository : CrudRepository<Room, Long> {

    fun findAllByCinemaId(cinemaId: Long): Iterable<Room>

    fun findByIdAndCinemaId(id: Long, cinema_Id: Long) : Room

    fun existsByName(name: String) : Boolean

    fun existsByIdAndCinemaId(id: Long, cinema_Id: Long) : Boolean
}