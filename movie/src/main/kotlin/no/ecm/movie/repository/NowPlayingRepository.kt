package no.ecm.movie.repository

import no.ecm.movie.model.entity.NowPlaying
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NowPlayingRepository : CrudRepository<NowPlaying, Long> {
}