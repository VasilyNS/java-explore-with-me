package ru.practicum.statserver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statdto.StatDto;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.tools.IncorrectRequestException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    @Transactional
    public StatDto saveStat(StatDto statDto) {
        Stat stat = StatMapper.toStat(statDto);
        return StatMapper.toStatDto(statRepository.save(stat));
    }

    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        // Проверка на корректность start и end
        if (start != null && end != null && start.isAfter(end)) {
            throw new IncorrectRequestException("The Start parameter cannot be after the End");
        }

        if (uris == null) { // Для всех uri

            if (!unique) { // Для повторяющихся ip
                return statRepository.getStatCountForAllIp(start, end);
            } else { // Все повторяющиеся ip считаем за один
                return statRepository.getStatCountForUniqueIp(start, end);
            }

        } else { // Для указанного массива uri

            if (!unique) { // Для повторяющихся ip
                return statRepository.getStatCountForAllIpByUris(start, end, uris);
            } else { // Все повторяющиеся ip считаем за один
                return statRepository.getStatCountForUniqueIpByUris(start, end, uris);
            }

        }
    }

}
