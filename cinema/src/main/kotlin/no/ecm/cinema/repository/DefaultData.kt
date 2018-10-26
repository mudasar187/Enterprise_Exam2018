package no.ecm.cinema.repository

import no.ecm.cinema.model.entity.Cinema
import no.ecm.cinema.model.entity.Room
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DefaultData(
        private var cinemaRepository: CinemaRepository,
        private var roomRepository: RoomRepository
) {

    @PostConstruct
    fun createData(){

        // Creating cinema
        val cinema1 = Cinema(name = "Klingenberg", location = "Oslo")
        cinemaRepository.save(cinema1)

        //Creating room
        val room1 = Room(name = "Sal 1", cinemaId = cinema1.id!!, seats = mutableSetOf("A1", "A2"))
        roomRepository.save(room1)

        // Finding room by name
        val room = roomRepository.findByName("Sal 1")
        print("\n"+room.cinemaId + "\t" + room.name)

        // find cinema by name
        val cinema = cinemaRepository.findByName("Klingenberg")
        print("\n"+cinema.location!! + "\n")

        // Creating 2. ciname in Oslo
        val cinema2 = Cinema(name = "Ringen", location = "Oslo")
        cinemaRepository.save(cinema2)

        // Finding all cinemas by location (city)
        val cinemas = cinemaRepository.findAllByLocationIgnoreCase("oslo")
        print("Number of cinemas in Oslo: ${cinemas.count()} \n")

        // Creating 2. room in cinema 1
        val room2 = Room(name = "sal 2", cinemaId = cinema1.id!!, seats = mutableSetOf("B1", "C1", "C2"))
        roomRepository.save(room2)

        val roomsInCinemaOne = roomRepository.findAllByCinemaId(cinema1.id!!)
        print("Number of rooms in Cinema 1: ${roomsInCinemaOne.count()} \n")

    }
}