package no.ecm.cinema.repository

import no.ecm.cinema.model.entity.Room
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoomRepository : CrudRepository<Room, Long> {

    fun findByName(name: String): Room

    fun findAllByCinemaId(cinemaId: Long): Iterable<Room>
}