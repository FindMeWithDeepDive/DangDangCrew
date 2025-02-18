package findme.dangdangcrew.place.domain;

import findme.dangdangcrew.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "favorite_places")
public class FavoritePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    private LocalDateTime createdAt;

    @Builder
    public FavoritePlace(User user, Place place, LocalDateTime createdAt){
        this.user = user;
        this.place = place;
        this.createdAt = createdAt;
    }
}
