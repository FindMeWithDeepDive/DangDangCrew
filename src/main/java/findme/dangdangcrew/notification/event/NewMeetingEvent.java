package findme.dangdangcrew.notification.event;

/**
 * @param placeName  즐겨찾기 한 장소 이름
 */
public record NewMeetingEvent(
        // targetUserId는 이벤트 리스너에서 추가해야할듯.
        Long targetUserId,
        String placeName,
        Long meetingId
) {
}
