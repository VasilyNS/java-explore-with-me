package ru.practicum.ewmservice.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Результат подтверждения/отклонения заявок на участие в событии
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResult {

    private List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

    private List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

}
