package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmservice.enums.*;
import ru.practicum.ewmservice.model.*;

import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

}
