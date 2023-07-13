package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmservice.enums.Status;
import ru.practicum.ewmservice.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r " +
            "FROM Request r " +
            "WHERE r.requester.id = :userId ")
    List<Request> getAllRequestsForCurrentUser(Long userId);

    @Query("SELECT r " +
            "FROM Request r " +
            "WHERE r.event.id = :eventId ")
    List<Request> getAllRequestsForUsersEvent(Long eventId);

    @Query("SELECT r " +
            "FROM Request r " +
            "WHERE r.event.id = :eventId " +
            "AND r.status IN :statuses ")
    List<Request> getRequestsByEventIDStatusList(Long eventId, List<Status> statuses);

}
