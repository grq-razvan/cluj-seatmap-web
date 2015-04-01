package seatmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import seatmap.entity.Participant;

public interface ParticipantRepository extends CrudRepository<Participant, Long> {

    @Query("SELECT p.name FROM Participant p")
    List<String> findNames();

}
