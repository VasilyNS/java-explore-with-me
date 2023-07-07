package ru.practicum.ewmservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmservice.enums.Status;

import java.util.List;

/**
 * Изменение статуса запроса на участие в событии текущего пользователя
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;  // Идентификаторы запросов на участие в событии текущего пользователя

    private Status status;          // Новый статус запроса на участие в событии текущего пользователя
                                    // Enum: CONFIRMED, REJECTED

}
