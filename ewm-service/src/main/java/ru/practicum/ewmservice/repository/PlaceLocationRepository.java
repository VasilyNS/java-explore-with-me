package ru.practicum.ewmservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmservice.model.PlaceLocation;

import java.util.List;

public interface PlaceLocationRepository extends JpaRepository<PlaceLocation, Long> {

    @Query("SELECT p " +
            "FROM PlaceLocation p ")
    List<PlaceLocation> getAllLocations(Pageable pageable);

}
