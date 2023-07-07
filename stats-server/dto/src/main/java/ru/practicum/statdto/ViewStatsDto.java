package ru.practicum.statdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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