package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.enums.*;
import ru.practicum.ewmservice.mapper.*;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.repository.*;
import ru.practicum.ewmservice.tools.exception.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final EventService eventService;

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     */
    @Transactional
    public ParticipationRequestDto saveRequest(Long userId, Long eventId) {
        Request request = requestMapper.toRequest(userId, eventId);
        Event event = request.getEvent(); // Получаем событие без доп. чтения в БД

        // Инициатор события не может добавить запрос на участие в своём событии (код ошибки 409)
        if (event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityFailureException("An event initiator cannot add a participation " +
                    "request to their event. userId=" + userId + ", eventId=" + eventId);
        }

        // Нельзя участвовать в неопубликованном событии (код ошибки 409)
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new DataIntegrityFailureException("You can't participate in an unpublished event. " +
                    "userId=" + userId + ", eventId=" + eventId);
        }

        // Если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (код ошибки 409)
        if (event.getParticipantLimit() != 0
                && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new DataIntegrityFailureException("The event has reached its limit of participation requests. " +
                    "userId=" + userId + ", eventId=" + eventId);
        }

        // Если для события отключена пре-модерация запросов на участие,
        // то запрос должен автоматически перейти в состояние подтвержденного
        if (!event.getRequestModeration() && event.getParticipantLimit() != 0) {
            eventService.incEventCountConfirmedRequests(eventId);
            request.setStatus(Status.CONFIRMED);
        }

        // При participantLimit == 0 также устанавливаем статус CONFIRMED вне зависимости от requestModeration
        // см. тест постмана "Добавление запроса на участие при participantLimit == 0"
        if (event.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED);
        }

        Request requestForReturn = requestRepository.save(request);
        return requestMapper.toParticipationRequestDtoFromRequest(requestForReturn);
    }

    /**
     * Отмена своего запроса на участие в событии
     */
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = checkExistAndGetRequest(requestId);

        if (!request.getRequester().getId().equals(userId)) { // Попытка отменить не свой запрос
            throw new DataIntegrityFailureException("Attempting to cancel a request that is not your own." +
                    " userId=" + userId + ", requestId=" + requestId);
        }

        request.setStatus(Status.CANCELED);
        Request requestForReturn = requestRepository.save(request);
        return requestMapper.toParticipationRequestDtoFromRequest(requestForReturn);
    }

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях (Private API)
     */
    public List<ParticipationRequestDto> getAllRequestsForCurrentUser(Long userId) {
        List<Request> requests = requestRepository.getAllRequestsForCurrentUser(userId);
        return requests.stream().map(requestMapper::toParticipationRequestDtoFromRequest).collect(Collectors.toList());
    }


    /**
     * Получение информации о запросах на участие в событии текущего пользователя (Private API)
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */
    public List<ParticipationRequestDto> getAllRequestsForUsersEvent(Long userId, Long eventId) {
        List<Request> requests = requestRepository.getAllRequestsForUsersEvent(eventId);
        return requests.stream().map(requestMapper::toParticipationRequestDtoFromRequest).collect(Collectors.toList());
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя (Private API)
     * <p>
     * - если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
     * - если при подтверждении данной заявки, лимит заявок для события исчерпан,
     * то все неподтверждённые заявки необходимо отклонить
     */
    public EventRequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId,
                                                               EventRequestStatusUpdateRequest requestsStatus) {
        // Получаем событие, далее работаем с ним, и в конце нужно для него поменять счетчик одобренных заявок
        Event event = eventService.checkExistAndGetEvent(eventId);

        // Проверка, что пользователь согласовывает заявки на своё событие
        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityFailureException("User can only approve requests for his event." +
                    " userId=" + userId + ", eventId=" + eventId);
        }

        // Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new DataIntegrityFailureException("You cannot approve requests if the approvals counter is full." +
                    " userId=" + userId + ", eventId=" + eventId + ", participantLimit=" + event.getParticipantLimit());
        }

        boolean fromHereOnlyRejections = false; // Флаг, что дальше только отказы
        int confirmedCount = 0; // Сколько заявок было одобрено

        for (Long requestId : requestsStatus.getRequestIds()) { // Для всех индексов в заявке
            Request request = checkExistAndGetRequest(requestId);
            if (request.getStatus().equals(Status.PENDING)) {
                if (requestsStatus.getStatus().equals(Status.CONFIRMED)) { // Согласовываем заявки
                    if (!fromHereOnlyRejections) {
                        request.setStatus(Status.CONFIRMED);
                        confirmedCount++;
                        // Проверяем счетчик согласованных заявок в событии, полюс количество согласованных в этом цикле
                        if (event.getParticipantLimit() != 0 &&
                                event.getConfirmedRequests() + confirmedCount >= event.getParticipantLimit()) {
                            // Переключаем в режим, когда все остальные заявки будут отклонены
                            fromHereOnlyRejections = true;
                        }
                    } else {
                        request.setStatus(Status.REJECTED);
                    }
                } else if (requestsStatus.getStatus().equals(Status.REJECTED)) { // Отказываем в заявке
                    request.setStatus(Status.REJECTED);
                } else {
                    throw new DataIntegrityFailureException("Status can only be CONFIRMED or REJECTED." +
                            " userId=" + userId + ", eventId=" + eventId + ", " + requestsStatus);
                }
                requestRepository.save(request);
            } else { // Статус можно изменить только у заявок, находящихся в состоянии ожидания (-> 409)
                throw new DataIntegrityFailureException("User is attempting to confirm or reject request " +
                        "that has status other than PENDING. userId=" + userId + ", eventId=" + eventId);
            }
        }

        if (event.getParticipantLimit() != 0 && confirmedCount > 0) {
            // Если были одобрены заявки, то сохраняем новое значение счетчика события в БД
            event.setConfirmedRequests(event.getConfirmedRequests() + confirmedCount);
            eventRepository.save(event);
        }

        // Подготавливаем общий ответ (result) по подтвержденным и отклоненным заявкам на событие
        List<Status> statuses = List.of(Status.CONFIRMED, Status.REJECTED);
        List<Request> requests = requestRepository.getRequestsByEventIDStatusList(eventId, statuses);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        ParticipationRequestDto participationRequestDto;

        for (Request request : requests) {
            participationRequestDto = requestMapper.toParticipationRequestDtoFromRequest(request);
            if (request.getStatus().equals(Status.CONFIRMED)) {
                result.getConfirmedRequests().add(participationRequestDto);
            } else {
                result.getRejectedRequests().add(participationRequestDto);
            }
        }

        return result;
    }

    /**
     * Проверка, что сущность есть в БД, если нет - исключение, если да - возврат объекта с ней
     */
    @Transactional(readOnly = true)
    public Request checkExistAndGetRequest(Long id) {
        return requestRepository.findById(id).orElseThrow(() -> new NotFoundException("Request " + id));
    }

}
