package seatmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import seatmap.entity.Seat;

public interface SeatRepository extends CrudRepository<Seat, Long> {

    @Query("SELECT s.seat FROM Seat s")
    List<String> findNames();

}
