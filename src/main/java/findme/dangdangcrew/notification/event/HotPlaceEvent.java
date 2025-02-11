package findme.dangdangcrew.notification.event;

/**
 * @param placeName 핫한 장소 이름
 */
public record HotPlaceEvent(
        String placeName,
        String placeId
) {
}
