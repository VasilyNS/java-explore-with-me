package ru.practicum.ewmservice.enums;

/**
 * Состояния события
 */
public enum StateAction {

    SEND_TO_REVIEW, CANCEL_REVIEW,  // Доступно для User, названия говорят сами за себя

    PUBLISH_EVENT, REJECT_EVENT     // Доступно для Admin

}
