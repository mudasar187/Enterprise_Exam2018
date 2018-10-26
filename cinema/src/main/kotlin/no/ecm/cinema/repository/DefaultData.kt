package no.ecm.cinema.repository

import no.ecm.cinema.model.entity.Cinema
import no.ecm.cinema.model.entity.Room
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DefaultData(
        private var cinemaRepository: CinemaRepository,
        private var roomRepository: RoomRepository

) {

    @PostConstruct
    fun createData(){

        val cinema1 = Cinema(name = "Klingenberg", location = "Oslo")

        cinemaRepository.save(cinema1)

        val room1 = Room(name = "Sal 1", cinema = cinema1, seats = mutableSetOf("A1", "A2"))

        roomRepository.save(room1)


        val room = roomRepository.findByName("Sal 1")

        print("\n"+room.cinema!!.name + "\t" + room.name)

        val cinema = cinemaRepository.findByName("Klingenberg")

        print("\n"+cinema.location!! + "\n")

    }
}