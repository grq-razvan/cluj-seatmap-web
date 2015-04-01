package seatmap.repository;

import org.springframework.data.repository.CrudRepository;

import seatmap.entity.Participant;

public interface ParticipantRepository extends CrudRepository<Participant, Long> {

}
