package findme.dangdangcrew.place.repository;

import findme.dangdangcrew.place.domain.FavoritePlace;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace, Long> {
    boolean existsByUserAndPlace(User user, Place place);

    @Query("SELECT fp FROM FavoritePlace fp JOIN FETCH fp.place WHERE fp.user = :user")
    List<FavoritePlace> findAllByUser(@Param("user") User user);

    @Query("SELECT fp FROM FavoritePlace fp JOIN FETCH fp.user WHERE fp.place.id = :placeId")
    List<FavoritePlace> findAllByPlaceId(@Param("placeId") String placeId);
}
