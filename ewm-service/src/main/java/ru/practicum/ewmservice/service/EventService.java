package ru.practicum.ewmservice.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.enums.*;
import ru.practicum.ewmservice.mapper.EventMapper;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.QEvent;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.tools.*;
import ru.practicum.ewmservice.tools.exception.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    @PersistenceContext
    private EntityManager entityManager; // Для QueryDSL

    /**
     * Добавление нового события
     */
    @Transactional
    public EventFullDto saveEvent(NewEventDto newEventDto, Long userId) {
        Event event = eventMapper.toEventFormNewEventDto(newEventDto, userId);

        Event eventForReturn = eventRepository.save(event);
        return eventMapper.toEventFullDtoFromEvent(eventForReturn);
    }

    /**
     * Изменение события добавленного текущим пользователем,
     */
    @Transactional
    public EventFullDto updateEventByUser(UpdateEventUserRequest updateEventUserRequest, Long userId, Long eventId) {
        Event event = checkExistAndGetEvent(eventId);

        // Если была попытка изменить чужое событие
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event " + eventId);
        }
        // Изменить можно только отмененные события или события в состоянии ожидания модерации
        if (!(event.getState().equals(State.CANCELED) || event.getState().equals(State.PENDING))) {
            throw new DataIntegrityFailureException("Only pending or canceled events can be changed"); // Код 409
        }

        // Редактируем все поля, которые пришли в updateEventUserRequest и не null
        event = eventMapper.toEventFromUpdateEventUserRequest(event, updateEventUserRequest);

        Event eventForReturn = eventRepository.save(event);
        return eventMapper.toEventFullDtoFromEvent(eventForReturn);
    }

    /**
     * Редактирование данных события и его статуса (отклонение/публикация).
     */
    @Transactional
    public EventFullDto updateEventByAdmin(UpdateEventAdminRequest updateEventAdminRequest, Long eventId) {
        Event event = checkExistAndGetEvent(eventId);

        // Предварительная проверка даты и ошибка 400, согласно тесту
        // "Изменение даты события на уже наступившую"
        if (updateEventAdminRequest.getEventDate() != null &&
                updateEventAdminRequest.getEventDate()
                        .isBefore(LocalDateTime.now())) {
            throw new IncorrectRequestException("The date of the event must not occur");
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            // Проверка условия: дата начала изменяемого события должна быть не ранее чем
            // за 1 час от даты публикации. (Ожидается код ошибки 409)
            // 1) Проверка, что этот апдейт на публикацию
            if (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                // 2) Изначально берем дату из ивента в базе
                LocalDateTime checkDate = event.getEventDate();
                // 3) Если дата события пришла в dto апдейта, то берем её
                if (updateEventAdminRequest.getEventDate() != null) {
                    checkDate = updateEventAdminRequest.getEventDate();
                }
                LocalDateTime checkDateMinusTime = checkDate.minusHours(1);
                // 4) Проверка на разницу менее 1 часа и выброс исключения
                if (checkDateMinusTime.isBefore(LocalDateTime.now())) {
                    throw new DataIntegrityFailureException("The start date of the event to be changed must "
                            + "be no earlier than 1 hour from the date of publication"); // Код 409
                }

                // Проверка условия: событие можно публиковать, только если оно в состоянии ожидания публикации (-> 409)
                if (!event.getState().equals(State.PENDING)) {
                    throw new DataIntegrityFailureException("The event can be published only if it is "
                            + "pending publication"); // Код 409
                }
            }

            // Проверка условия: событие можно отклонить, только если оно еще не опубликовано (Иначе искл. 409)
            if (updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT)) {
                if (event.getState().equals(State.PUBLISHED)) {
                    throw new DataIntegrityFailureException("The event can only be rejected if it has not "
                            + "yet been published"); // Код 409
                }
            }
        }

        // Редактируем все поля, которые пришли в updateEventUserRequest и не null
        event = eventMapper.toEventFromUpdateEventAdminRequest(event, updateEventAdminRequest);

        Event eventForReturn = eventRepository.save(event);
        return eventMapper.toEventFullDtoFromEvent(eventForReturn);
    }

    /**
     * Получение событий, добавленных текущим пользователем
     */
    public List<EventFullDto> getAllEventsForCurrentUser(Long userId, Long from, Long size) {
        QEvent qEvent = QEvent.event;
        List<Event> events = new JPAQueryFactory(entityManager)
                .selectFrom(qEvent)
                .where(qEvent.initiator.id.eq(userId))
                .offset(from)
                .limit(size)
                .fetch();
        return events.stream().map(eventMapper::toEventFullDtoFromEvent).collect(Collectors.toList());
    }

    /**
     * Получение полной информации о событии добавленном текущим пользователем PUB API (пример: GET /users/1/events/1)
     */
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdForCurrentUser(Long userId, Long eventId) {
        Event event = checkExistAndGetEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) { // Если была попытка смотреть подробности чужого события:
            throw new NotFoundException("Event " + eventId);
        }
        return eventMapper.toEventFullDtoFromEvent(event);
    }

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору
     */
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdForPublicApi(Long id) {
        Event event = eventRepository.getEventByIdForPublicApi(State.PUBLISHED, id)
                .orElseThrow(() -> new NotFoundException("Event " + id));
        return eventMapper.toEventFullDtoFromEvent(event);
    }

    /**
     * Метод длинный, в рабочем варианте можно сделать его рефакторинг не несколько методов.
     * Но в таком варианте он проще воспринимается для меня.
     */
    @Transactional(readOnly = true)
    public List<EventFullDto> getSelectedEventsForPublic(ParamsForSearch params) {
        boolean isSortByViews = false; // Глобальный переключатель для работы метода

        // Для сложных "наборных" запросов используем QueryDSL, как самый гибкий инструмент составления запросов
        QEvent qEvent = QEvent.event;
        BooleanBuilder whereClause = new BooleanBuilder();

        // "Это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события"
        whereClause.and(qEvent.state.eq(State.PUBLISHED));

        // Текст (text) для поиска в содержимом аннотации (annotation) и подробном описании события (description)
        // текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
        if (params.getText() != null) {
            whereClause.and(qEvent.annotation.containsIgnoreCase(params.getText())
                    .or(qEvent.description.containsIgnoreCase(params.getText())));
        }

        // Список идентификаторов категорий (categories) в которых будет вестись поиск
        if (params.getCategories() != null) {
            whereClause.and(qEvent.category.id.in(params.getCategories())); // .id!
        }

        // Поиск только платных/бесплатных событий
        if (params.getPaid() != null) {
            whereClause.and(qEvent.paid.eq(params.getPaid()));
        }

        // Если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события,
        // которые произойдут позже текущей даты и времени
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DT_PATTERN);
        LocalDateTime rangeStartLdt;
        LocalDateTime rangeEndLdt;
        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            rangeStartLdt = LocalDateTime.parse(params.getRangeStart(), formatter);
            rangeEndLdt = LocalDateTime.parse(params.getRangeEnd(), formatter);
            whereClause.and(qEvent.eventDate.between(rangeStartLdt, rangeEndLdt));
            // Проверка на корректность start и end
            if (rangeStartLdt.isAfter(rangeEndLdt)) {
                throw new IncorrectRequestException("The Start parameter cannot be after the End");
            }
        } else {
            rangeStartLdt = LocalDateTime.now();
            whereClause.and(qEvent.eventDate.after(rangeStartLdt));
        }

        // Только события у которых не исчерпан лимит запросов на участие
        if (params.getOnlyAvailable() != null && params.getOnlyAvailable()) {
            whereClause.and(qEvent.participantLimit.ne(qEvent.confirmedRequests)
                    .or(qEvent.participantLimit.eq(0)));
        }

        OrderSpecifier<?> orderSpecifier = qEvent.id.asc(); // Дефолтная сортировка, если не задан sort
        // Вариант сортировки: по дате события или по количеству просмотров
        if (params.getSort() != null) {
            if (params.getSort().equals(Sort.EVENT_DATE.toString())) {
                orderSpecifier = qEvent.eventDate.desc();
            } else if (params.getSort().equals(Sort.VIEWS.toString())) {
                isSortByViews = true; // Переключаем выдачу данных по сортировке в другой режим
            } else {
                throw new IncorrectRequestException("The 'sort' parameter is not correct: " + params.getSort()); // 400
            }
        }

        if (!isSortByViews) {
            // Обычный вариант, когда не используем сортировку по просмотрам (без обращений в другую БД)
            List<Event> events = new JPAQueryFactory(entityManager)
                    .selectFrom(qEvent)
                    .where(whereClause)
                    .orderBy(orderSpecifier)
                    .offset(params.getFrom())
                    .limit(params.getSize())
                    .fetch();
            return events.stream().map(eventMapper::toEventFullDtoFromEvent)
                    .collect(Collectors.toList());
        } else {
            /*
            Сортировка по просмотрам.
            События и информация для подсчета просмотров хранятся в разных базах. Без дополнительных таблиц в
            любой из этих баз мы не можем написать эффективную выборку с глобальной сортировкой для боевой базы
            с многими миллионами событий, пользователей и большой нагрузкой на серверы.
            Данный вопрос разобран в чате когорты с наставником Владимиром Ивановым

            Поэтому далее идет максимально простой и наглядный, но не слишком оптимизированный код,
            но дающий абсолютно верные выходные данные, также как было бы с глобальной сортировкой.
            Данный код будет работать на сотнях и тысячах событий, но не на миллионах.
            */

            // Чтение без пагинации
            List<Event> events = new JPAQueryFactory(entityManager)
                    .selectFrom(qEvent)
                    .where(whereClause)
                    .orderBy(orderSpecifier)
                    .fetch(); // читаем

            // Создаем массив EventFullDto, где уже будут просмотры в поле Long views
            List<EventFullDto> eventsFullDto = events.stream().map(eventMapper::toEventFullDtoFromEvent)
                    .collect(Collectors.toList());

            // Сортировка по просмотрам, первые с наибольшим количеством просмотров
            eventsFullDto.sort(Comparator.comparing(EventFullDto::getViews).reversed());

            // И только из отсортированного массива уже выборка согласно пагинации
            List<EventFullDto> eventsFullDtoWithPages = new ArrayList<>();
            int f = Math.toIntExact(params.getFrom());
            int s = Math.toIntExact(params.getSize());
            for (int i = 0; i < s; i++) {
                if (f + i < eventsFullDto.size()) { // Проверка на невыход за диапазон списка
                    eventsFullDtoWithPages.add(eventsFullDto.get(f + i));
                }
            }

            return eventsFullDtoWithPages;
        }
    }

    /**
     * Поиск событий (Admin API)
     * Наборные условия для QueryDSL в зависимости от того, что задали в uri
     */
    public List<EventFullDto> getSelectedEventForAdmin(ParamsForSearch params) {
        QEvent qEvent = QEvent.event;
        BooleanBuilder whereClause = new BooleanBuilder();

        // Список id пользователей, чьи события нужно найти
        if (params.getUsers() != null) {
            whereClause.and(qEvent.initiator.id.in(params.getUsers()));
        }

        // Список состояний в которых находятся искомые события
        List<State> states = new ArrayList<>(); // Список из enum State, для оператора in
        if (params.getStates() != null) {
            for (String state : params.getStates()) {
                states.add(State.valueOf(state)); // При ошибке в valueOf будет IllegalArgumentException
            }
            whereClause.and(qEvent.state.in(states));
        }

        // Список идентификаторов категорий (categories) в которых будет вестись поиск
        if (params.getCategories() != null) {
            whereClause.and(qEvent.category.id.in(params.getCategories())); // .id!
        }

        // rangeStart и rangeEnd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DT_PATTERN);
        LocalDateTime rangeStartLdt;
        LocalDateTime rangeEndLdt;
        if (params.getRangeStart() != null) {
            rangeStartLdt = LocalDateTime.parse(params.getRangeStart(), formatter);
            whereClause.and(qEvent.eventDate.after(rangeStartLdt));
        }
        if (params.getRangeEnd() != null) {
            rangeEndLdt = LocalDateTime.parse(params.getRangeEnd(), formatter);
            whereClause.and(qEvent.eventDate.before(rangeEndLdt));
        }

        List<Event> events = new JPAQueryFactory(entityManager)
                .selectFrom(qEvent)
                .where(whereClause)
                .offset(params.getFrom())
                .limit(params.getSize())
                .fetch();
        return events.stream().map(eventMapper::toEventFullDtoFromEvent)
                .collect(Collectors.toList());
    }

    /**
     * Увеличиваем счетчик confirmedRequests события, а если он уже заполнен до лимита, то исключение и код ошибки 409
     */
    @Transactional
    public void incEventCountConfirmedRequests(Long id) {
        Event event = checkExistAndGetEvent(id);
        if (event.getConfirmedRequests() < event.getParticipantLimit()) {
            eventRepository.incEventCountConfirmedRequests(id);
        } else {
            throw new DataIntegrityFailureException("The event has reached its limit of participation requests. " +
                    "eventId=" + id + ", limit=" + event.getParticipantLimit());
        }
    }

    /**
     * Проверка, что сущность есть в БД, если нет - исключение, если да - возврат объекта с ней
     */
    @Transactional(readOnly = true)
    public Event checkExistAndGetEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event " + id));
    }

}
