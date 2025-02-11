package findme.dangdangcrew.chat.entity;

import findme.dangdangcrew.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private static final int MAX_PARTICIPANTS = 20;
    private int currentParticipants = 0;

    public ChatRoom(User owner, String title) {
        this.owner = owner;
        this.title = title;
        this.currentParticipants = 1; // 방장이 자동으로 입장
    }

    public boolean addParticipant() {
        if (currentParticipants < MAX_PARTICIPANTS) {
            currentParticipants++;
            return true;
        }
        return false;
    }

    public void removeParticipant() {
        if (currentParticipants > 0) {
            currentParticipants--;
        }
    }
}
