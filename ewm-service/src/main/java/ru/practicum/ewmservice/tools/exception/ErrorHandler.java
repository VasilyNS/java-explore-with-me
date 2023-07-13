package ru.practicum.ewmservice.tools.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.DateTimeException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    // Ответ 400 при исключении ошибки валидации через аннотации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMsg = bindingResult.getFieldErrors().stream()
                .map(error -> "Field: " + error.getField() + ". Error: " + error.getDefaultMessage()
                        + ". Value: " + error.getRejectedValue())
                .reduce((error1, error2) -> error1 + "; " + error2)
                .orElse("Validation failed");
        return All400Errors(errorMsg);
    }

    // Исключение при ошибке когда нет требуемого параметра в эндпоинте
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return All400Errors(e.getMessage());
    }

    // Ответ 400 для прочих ошибок выбрасываемое "вручную"
    @ExceptionHandler(IncorrectRequestException.class)
    public ResponseEntity handleIncorrectRequestException(IncorrectRequestException e) {
        return All400Errors(e.getMessage());
    }

    // Исключение при ошибке конвертации данных выбрасываемое автоматически
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return All400Errors(e.getMessage());
    }

    // Исключение при ошибке конвертации данных выбрасываемое автоматически
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity handleNumberFormatException(NumberFormatException e) {
        return All400Errors(e.getMessage());
    }

    // Исключение при ошибке конвертации данных выбрасываемое автоматически
    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity handleDateTimeException(DateTimeException e) {
        return All400Errors(e.getMessage());
    }

    // Исключение при ошибке конвертации данных выбрасываемое автоматически
    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity handleConversionFailedException(ConversionFailedException e) {
        return All400Errors(e.getMessage());
    }

    // Исключение при ошибке конвертации данных выбрасываемое автоматически
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity handleIllegalStateException(IllegalStateException e) {
        return All400Errors(e.getMessage());
    }

    private ResponseEntity all400Errors(String errorMsg) {
        HttpStatus code = HttpStatus.BAD_REQUEST; // 400
        ApiError response = new ApiError(code, "Incorrectly made request.", errorMsg);

        log.warn(errorMsg);
        return ResponseEntity.status(code).body(response);
    }

    // Ответ при исключении, что объект отсутствует
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(NotFoundException e) {
        String[] errorMsgs = e.getMessage().split(" ");
        String errorMsg = errorMsgs[0] + " with id=" + errorMsgs[1] + " was not found";
        HttpStatus code = HttpStatus.NOT_FOUND; // 404
        ApiError response = new ApiError(code, "The required object was not found.", errorMsg);

        log.warn(errorMsg);
        return ResponseEntity.status(code).body(response);
    }

    // Ответ при нарушении уникального значения в поле таблицы (ошибка целостности данных)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String errorMsg = e.getMessage();
        HttpStatus code = HttpStatus.CONFLICT; // 409
        ApiError response = new ApiError(code, "Integrity constraint has been violated.", errorMsg);

        log.warn(errorMsg);
        return ResponseEntity.status(code).body(response);
    }

    // Ответ при прочих исключениях - непредвиденная ошибка сервера
    @ExceptionHandler(Throwable.class)
    public ResponseEntity handleThrowable(Throwable e) { // final Throwable e
        String errorMsg = e.getMessage();
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        ApiError response = new ApiError(code, "Unexpected error.", errorMsg);

        log.warn(errorMsg);
        return ResponseEntity.status(code).body(response);
    }

}
