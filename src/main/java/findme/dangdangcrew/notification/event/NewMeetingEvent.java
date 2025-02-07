package findme.dangdangcrew.notification.event;

/**
 * @param placeName  즐겨찾기 한 장소 이름
 */
public record NewMeetingEvent(
        Long targetUserId,
        String placeName,
        Long meetingId) {
}
