package seatmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import seatmap.entity.Room;

public interface RoomRepository extends CrudRepository<Room, Long> {

    @Query("SELECT r.room FROM Room r")
    List<String> findNames();

}
