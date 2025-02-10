package findme.dangdangcrew.place.repository;

import findme.dangdangcrew.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, String> {
}
