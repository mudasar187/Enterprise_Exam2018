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

        val seats = mutableSetOf(
                "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "A12", "A13", "A14",
                "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10", "B11", "B12", "B13", "B14",
                "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10", "C11", "C12", "C13", "C14",
                "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D10", "D11", "D12", "D13", "D14",
                "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "E10", "E11", "E12", "E13", "E14",
                "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "F13", "F14",
                "G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9", "G10", "G11", "G12", "G13", "G14")

        // Creating cinema
        val klingenberg = Cinema(name = "Klingenberg", location = "Oslo")

        // Creating 2. ciname in Oslo
        val ringen = Cinema(name = "Ringen", location = "Oslo")

        // Creating ciname in Bergen
        val bergen = Cinema(name = "Bergen Kino", location = "Bergen")

        cinemaRepository.saveAll(mutableListOf(klingenberg, ringen, bergen))

        //Creating room in Klingenberg
        val room1 = Room(name = "Sal 1", cinema = klingenberg, seats = seats)

        // Creating 2. room in Klingenberg
        val room2 = Room(name = "sal 2", cinema = klingenberg, seats = seats)

        // Creating room in Ringen
        val room3 = Room(name = "sal 1", cinema = ringen, seats = seats)

        // Creating room in Bergen
        val room4 = Room(name = "sal 1", cinema = bergen, seats = seats)

        roomRepository.saveAll(mutableListOf(room1, room2, room3, room4))
    }
}