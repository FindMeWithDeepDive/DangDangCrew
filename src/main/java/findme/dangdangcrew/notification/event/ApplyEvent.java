package findme.dangdangcrew.notification.event;

/**
 * @param targetUserId 알림 받을 유저 (모임 방장)
 * @param nickname 모임 참가 요청한 유저
 * @param meetingName 모임 이름
 **/
public record ApplyEvent(
        Long targetUserId,
        String nickname,
        Long applyUserId,
        Long meetingId,
        String meetingName) {
}
