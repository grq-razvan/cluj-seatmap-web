package seatmap.repository;

import org.springframework.data.repository.CrudRepository;

import seatmap.entity.Seat;

public interface SeatRepository extends CrudRepository<Seat, Long> {

}
