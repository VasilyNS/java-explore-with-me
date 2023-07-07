package ru.practicum.ewmservice.tools.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Сведения об ошибке
 */
@Getter
@Setter
public class ApiError {

    private HttpStatus status;  // Код статуса HTTP-ответа
    private String reason;      // Общее описание причины ошибки
    private String message;     // Сообщение об ошибке

    // Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiError(HttpStatus status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
    }

}
