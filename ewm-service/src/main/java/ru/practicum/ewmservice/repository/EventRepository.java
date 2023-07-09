package ru.practicum.ewmservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewmservice.dto.CategoryDto;
import ru.practicum.ewmservice.enums.State;
import ru.practicum.ewmservice.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.state = :state " +
            "AND e.id = :id")
    Optional<Event> getEventByIdForPublicApi(State state, Long id);

}
