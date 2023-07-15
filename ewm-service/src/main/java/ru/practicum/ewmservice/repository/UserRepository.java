package ru.practicum.ewmservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.model.*;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT new ru.practicum.ewmservice.dto.UserDto(u.id, u.name, u.email) " +
            "FROM User u " +
            "ORDER BY u.id ASC ")
    List<UserDto> findAllUsers(Pageable pageable);

    @Query("SELECT new ru.practicum.ewmservice.dto.UserDto(u.id, u.name, u.email) " +
            "FROM User u " +
            "WHERE u.id IN :ids " +
            "ORDER BY u.id ASC ")
    List<UserDto> findUsersByIds(List<Long> ids, Pageable pageable);

}
