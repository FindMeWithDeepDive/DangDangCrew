package findme.dangdangcrew.notification.event;

/**
 * @param placeName  즐겨찾기 한 장소 이름
 */
public record NewMeetingEvent(
        String placeName,
        Long meetingId,
        String placeId
) {
}
