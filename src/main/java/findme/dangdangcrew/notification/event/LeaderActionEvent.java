package findme.dangdangcrew.notification.event;

/**
 * @param targetUserId 알림 받을 유저 (모임 참가 희망자)
 * @param meetingName 모임 이름
 **/
public record LeaderActionEvent(
        Long targetUserId,
        Long meetingId,
        String meetingName,
        LeaderActionType leaderActionType
) {
}
