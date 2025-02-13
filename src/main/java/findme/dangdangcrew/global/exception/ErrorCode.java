package findme.dangdangcrew.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // User Error
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),

    // FavoritePlace Error
    ALREADY_FAVORITE_PLACE(HttpStatus.BAD_REQUEST, "이미 즐겨찾기한 장소입니다."),

    // Kakao Map API Error
    DOCUMENTS_NOT_FOUND(HttpStatus.BAD_REQUEST, "Kakao API 응답에서 'documents' 필드를 찾을 수 없습니다."),
    INVALID_DATA_TYPE(HttpStatus.BAD_REQUEST,"Kakao API 응답 데이터 형식이 잘못되었습니다."),

    // Notification Error
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 알림입니다."),
    INVALID_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST, "지원되지 않는 알림 타입입니다."),

    // Meeting Error
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 미팅을 찾을 수  없습니다."),
    MEETING_EXCEED_LIMIT_PEOPLE(HttpStatus.BAD_REQUEST, "현재 인원은 최대 인원을 초과할 수 없습니다."),
    MEETING_INSUFFICIENT_PEOPLE(HttpStatus.BAD_REQUEST, "현재 인원은 1명 미만이 될 수 없습니다."),

    // Invalidation Error
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "요청한 JSON 형식이 올바르지 않습니다."),

    // JWT Token Error
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    MISSING_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 누락되었습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
