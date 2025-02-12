package findme.dangdangcrew.global.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @RequestParam, @PathVariable의 유효성 검사를 수행
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException ex){
        log.error("[Validated] 유효성 검사 예외 발생 msg:{}",ex.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_PARAMETER;
        List<ErrorResponseDto.ValidationError> validationErrors = extractValidationErrors(ex.getConstraintViolations());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDto.of(errorCode, validationErrors));
    }

    // 커스템 예외를 검사를 수행
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> customException(CustomException ex){
        log.error("예외 발생 msg:{}",ex.getErrorCode().getMessage());
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = errorCode.getStatus();
        String errorMessage = errorCode.getMessage();
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponseDto.of(status, errorMessage));
    }

    //  JSON 데이터를 DTO로 변환하는 과정에서 유효성 검사를 수행
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        ErrorCode errorCode = ErrorCode.INVALID_PARAMETER;
        List<ErrorResponseDto.ValidationError> validationErrors = extractValidationErrors(ex.getBindingResult());
        log.error("[Valid] 유효성 검사 예외 발생 errors: {}", validationErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDto.of(errorCode, validationErrors));
    }
    
    // json 형식 검사를 수행
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_JSON_FORMAT;
        String errorMessage = errorCode.getMessage() + " : " + ex.getMessage();
        HttpStatus status = errorCode.getStatus();
        log.error("예외 발생 msg: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDto.of(status, errorMessage));
    }

    private List<ErrorResponseDto.ValidationError> extractValidationErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(ErrorResponseDto.ValidationError::of)
                .collect(Collectors.toList());
    }

    private List<ErrorResponseDto.ValidationError> extractValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
        return constraintViolations
                .stream()
                .map(ErrorResponseDto.ValidationError::of)
                .collect(Collectors.toList());
    }
}
