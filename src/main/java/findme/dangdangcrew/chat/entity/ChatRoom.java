package findme.dangdangcrew.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private static final int MAX_PARTICIPANTS = 20;
    private int currentParticipants = 0;

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
