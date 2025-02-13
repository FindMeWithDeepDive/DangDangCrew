package findme.dangdangcrew.meeting.entity.enums;

public enum UserMeetingStatus {
    CANCELLED, // 예약취소
    CONFIRMED, // 확정
    WAITING, // 신청 후 대기 중
    LEADER, // 모임 생성자
    REMOVED // 모임이 삭제 됐을 경우
}
