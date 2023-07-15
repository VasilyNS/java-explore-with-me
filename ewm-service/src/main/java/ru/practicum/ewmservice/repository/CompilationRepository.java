package ru.practicum.ewmservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.ewmservice.model.*;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c " +
            "FROM Compilation c ")
    List<Compilation> getCompilations(Pageable pageable);

    @Query("SELECT c " +
            "FROM Compilation c " +
            "WHERE c.pinned = :pinned ")
    List<Compilation> getCompilationsByPinned(Boolean pinned, Pageable pageable);

}
