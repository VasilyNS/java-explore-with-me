package ru.practicum.statdto;

import lombok.*;

/**
 * DTO для получения статистики hits - счетчик
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto {

    private String app;
    private String uri;
    private Long hits;

}