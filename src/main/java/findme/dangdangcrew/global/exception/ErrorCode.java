package findme.dangdangcrew.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // User Error
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),

    // User Register Error
    EMAIL_ALREADY_IN_USE(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_IN_USE(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
    PHONE_NUMBER_ALREADY_IN_USE(HttpStatus.BAD_REQUEST, "이미 사용 중인 전화번호입니다."),
    WEAK_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호는 최소 6자리 이상이어야 합니다."),

    // Login Error
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // Place Error
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 장소입니다."),

    // FavoritePlace Error
    ALREADY_FAVORITE_PLACE(HttpStatus.BAD_REQUEST, "이미 즐겨찾기한 장소입니다."),

    // Kakao Map API Error
    DOCUMENTS_NOT_FOUND(HttpStatus.BAD_REQUEST, "Kakao API 응답에서 'documents' 필드를 찾을 수 없습니다."),
    INVALID_DATA_TYPE(HttpStatus.BAD_REQUEST,"Kakao API 응답 데이터 형식이 잘못되었습니다."),

    // Notification Error
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 알림입니다."),
    INVALID_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST, "지원되지 않는 알림 타입입니다."),
    NOTIFICATION_OWNER_MISMATCH(HttpStatus.BAD_REQUEST, "자신의 알림만 조회 할 수 있습니다."),

    // Meeting Error
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 미팅을 찾을 수  없습니다."),
    MEETING_EXCEED_LIMIT_PEOPLE(HttpStatus.BAD_REQUEST, "현재 인원은 최대 인원을 초과할 수 없습니다."),
    MEETING_INSUFFICIENT_PEOPLE(HttpStatus.BAD_REQUEST, "현재 인원은 1명 미만이 될 수 없습니다."),
    NOT_LEADER(HttpStatus.BAD_REQUEST, "현재 로그인한 유저는 리더가 아닙니다."),
    NOT_CHANGE(HttpStatus.BAD_REQUEST, "이전 상태와 동일합니다."),

    // Invalidation Error
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "요청한 JSON 형식이 올바르지 않습니다."),

    // Chat Error
    CHAT_ROOM_FULL(HttpStatus.BAD_REQUEST, "채팅방이 가득 찼습니다. (최대 20명)"),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."),
    ALREADY_JOINED(HttpStatus.BAD_REQUEST, "이미 채팅방에 참여한 사용자입니다."),
    ALREADY_LEFT(HttpStatus.BAD_REQUEST, "이미 채팅방을 나간 사용자입니다."),

    // JWT Token Error
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    MISSING_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 누락되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    INVALID_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "잘못된 인증 정보입니다."),

    // LeaderType Error
    INVALID_LEADER_ACTION_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 LeaderActionType 입니다."),

    // Evaluation Error
    SELF_EVALUATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신을 평가할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
