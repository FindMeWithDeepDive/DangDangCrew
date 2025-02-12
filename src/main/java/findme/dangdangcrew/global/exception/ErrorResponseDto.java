package findme.dangdangcrew.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;

@Builder
public record ErrorResponseDto(
        HttpStatus code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) // errors가 비어있으면 JSON 응답에서 제외
        List<ValidationError> validationErrors
) {
    public static ErrorResponseDto of(ErrorCode errorCode, List<ValidationError> validationErrors){
        return ErrorResponseDto.builder()
                .code(errorCode.getStatus())
                .message(errorCode.getMessage())
                .validationErrors(Optional.ofNullable(validationErrors).orElse(List.of())) // null 방지
                .build();
    }
    public static ErrorResponseDto of(HttpStatus status, String errorMessage){
        return ErrorResponseDto.builder()
                .code(status)
                .message(errorMessage)
                .build();
    }
    public record ValidationError(String field, String message) {
        public static ValidationError of(final FieldError fieldError) {
            return new ValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        public static ValidationError of(final ConstraintViolation<?> constraintViolation){
            return new ValidationError(
                    extractFieldName(constraintViolation.getPropertyPath()), // 필드명만 추출
                    constraintViolation.getMessage()
            );
        }
        private static String extractFieldName(Path propertyPath) {
            String[] pathElements = propertyPath.toString().split("\\.");
            return pathElements[pathElements.length - 1]; // 마지막 요소가 필드명
        }
    }
}
