package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmservice.enums.State;
import ru.practicum.ewmservice.model.Event;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.state = :state " +
            "AND e.id = :id")
    Optional<Event> getEventByIdForPublicApi(State state, Long id);

    @Modifying
    @Query("UPDATE Event e " +
            "SET e.confirmedRequests = e.confirmedRequests + 1 " +
            "WHERE e.id = :id ")
    void incEventCountConfirmedRequests(Long id);

}
