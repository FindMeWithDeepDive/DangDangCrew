package findme.dangdangcrew.chat.entity;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "meeting_id", nullable = false, unique = true)
    private Meeting meeting;

    private static final int MAX_PARTICIPANTS = 20;
    private int currentParticipants = 0;

    public ChatRoom(Meeting meeting) {
        this.meeting = meeting;
        this.currentParticipants = 1; // 방장이 자동으로 입장
    }

    public void addParticipant() {
        if (currentParticipants >= MAX_PARTICIPANTS) {
            throw new CustomException(ErrorCode.CHAT_ROOM_FULL);
        }
        currentParticipants++;
    }

    public void removeParticipant() {
        if (currentParticipants > 0) {
            currentParticipants--;
        }
    }
}
