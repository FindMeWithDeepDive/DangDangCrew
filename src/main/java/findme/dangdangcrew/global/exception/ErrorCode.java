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
    INVALID_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST, "지원되지 않는 알림 타입입니다.")
    ;




    private final HttpStatus status;
    private final String message;
}
